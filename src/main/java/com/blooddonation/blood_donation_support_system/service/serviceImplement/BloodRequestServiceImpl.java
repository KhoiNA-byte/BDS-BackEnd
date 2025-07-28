package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.*;
import com.blooddonation.blood_donation_support_system.entity.BloodRequest;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import com.blooddonation.blood_donation_support_system.enums.BloodRequestStatus;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import com.blooddonation.blood_donation_support_system.enums.Urgency;
import com.blooddonation.blood_donation_support_system.mapper.BloodRequestMapper;
import com.blooddonation.blood_donation_support_system.mapper.ProfileMapper;
import com.blooddonation.blood_donation_support_system.repository.BloodRequestRepository;
import com.blooddonation.blood_donation_support_system.repository.ProfileRepository;
import com.blooddonation.blood_donation_support_system.service.IBloodRequestService;
import com.blooddonation.blood_donation_support_system.service.MedicalFacilityStockService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

@Service
public class BloodRequestServiceImpl implements IBloodRequestService {
    @Autowired
    private BloodRequestRepository bloodRequestRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private MedicalFacilityStockService medicalFacilityStockService;
    @Autowired
    private ProfileServiceImpl profileService;
    private final PriorityBlockingQueue<BloodRequestDto> bloodRequestQueue;
    private final PriorityBlockingQueue<BloodRequestDto> pendingRequestQueue;
    private final int HIGH_URGENCY_DELAY = 0;
    private final int MEDIUM_URGENCY_DELAY = 1;
    private final int LOW_URGENCY_DELAY = 2;

    public BloodRequestServiceImpl() {
        Comparator<BloodRequestDto> comparator = Comparator
                .comparingInt(BloodRequestDto::calculatePriority).reversed()
                .thenComparing(BloodRequestDto::getCreatedTime);

        this.bloodRequestQueue = new PriorityBlockingQueue<>(11, comparator);
        this.pendingRequestQueue = new PriorityBlockingQueue<>(11, comparator);
    }

    @PostConstruct
    @Transactional(readOnly = true)
    public void initQueues() {
        List<BloodRequest> allRequests = bloodRequestRepository.findAllWithComponents();
        for (BloodRequest request : allRequests) {
            BloodRequestDto dto = BloodRequestMapper.toBloodRequestDto(request);
            if (dto.getStatus() == BloodRequestStatus.PROCESSING && dto.isAutomation()) {
                bloodRequestQueue.add(dto);
            } else if (dto.getStatus() == BloodRequestStatus.PENDING && dto.isAutomation()) {
                pendingRequestQueue.add(dto);
            }
        }
    }

    @Override
    @Transactional
    public BloodRequestDto createBloodRequest(BloodRequestDto bloodRequestDto) {
        if (bloodRequestDto.getCreatedTime() == null) {
            bloodRequestDto.setCreatedTime(LocalDateTime.now());
        }
        bloodRequestDto.setStatus(BloodRequestStatus.PENDING);
        Profile profile = null;
        if(bloodRequestDto.getProfile() == null) {
            profile = ProfileMapper.toEntity(profileService.getProfileById(bloodRequestDto.getProfileId()));
        } else {
            profile = ProfileMapper.toEntity(profileService.saveProfile(bloodRequestDto.getProfile()));
        }

        BloodRequest savedEntity = bloodRequestRepository.save(BloodRequestMapper.toBloodRequestEntity(bloodRequestDto, profile));
        BloodRequestDto bloodRequest = BloodRequestMapper.toBloodRequestDto(savedEntity);
        if(!bloodRequest.isAutomation()) return bloodRequest;
        int newPriority = bloodRequest.calculatePriority();
        boolean isHighPriority = bloodRequest.getUrgency() == Urgency.HIGH;
        List<ComponentType> componentTypes = bloodRequest.getComponentRequests()
                .stream().map(ComponentRequestDto::getComponentType).collect(Collectors.toList());
        List<MedicalFacilityStockDto> stockDtos = medicalFacilityStockService
                .getAvailableBloodByType(bloodRequestDto.getBloodType(), componentTypes);
        boolean isStockAvailable = true;
        for (ComponentRequestDto compReq : bloodRequest.getComponentRequests()) {
            MedicalFacilityStockDto stock = stockDtos.stream()
                    .filter(s -> s.getComponentType() == compReq.getComponentType())
                    .findFirst().orElse(null);
            if (stock == null || stock.getVolume() < compReq.getVolume()) {
                isStockAvailable = false;
                break;
            }
        }
        if (!isStockAvailable && isHighPriority) {
            return handleHighPriorityRequest(bloodRequest, stockDtos, newPriority);
        } else if (!isStockAvailable) {
            bloodRequest.setStatus(BloodRequestStatus.PENDING);
            pendingRequestQueue.add(bloodRequest);
            updateBloodRequestStatus(bloodRequest.getId(), BloodRequestStatus.PENDING);
            return bloodRequest;
        }
        bloodRequest.setStatus(BloodRequestStatus.PROCESSING);
        medicalFacilityStockService.withdrawBloodFromStock(bloodRequest);
        bloodRequestQueue.add(bloodRequest);
        updateBloodRequestStatus(bloodRequest.getId(), BloodRequestStatus.PROCESSING);
        return bloodRequest;
    }


    @Override
    @Transactional(readOnly = true)
    public List<BloodRequestDto> findAllBloodRequest() {
        List<BloodRequest> bloodRequests = bloodRequestRepository.findAllWithComponents();
        return bloodRequests.stream().map(BloodRequestMapper::toBloodRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BloodRequestDto findBloodRequestById(int id) {
        BloodRequest bloodRequest = bloodRequestRepository.findById(id);
        return BloodRequestMapper.toBloodRequestDto(bloodRequest);
    }


    @Override
    public BloodRequestDto addBloodRequestDonor(BloodRequestDto bloodRequestDto, BloodUnitDto bloodUnitDto, ProfileDto profileDto) {
        if (bloodRequestDto.getBloodUnits() == null) {
            bloodRequestDto.setBloodUnits(new ArrayList<>());
        }
        if (profileDto.getId() == null) {
            profileDto = profileService.saveProfile(profileDto);
        }
        bloodUnitDto.setProfileId(profileDto.getId());
        bloodRequestDto.getBloodUnits().add(bloodUnitDto);

        // Get the profile for the blood request
        Profile profile = profileRepository.findById(bloodRequestDto.getProfileId())
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with ID: " + bloodRequestDto.getProfileId()));

        BloodRequest bloodRequest = bloodRequestRepository.save(
                BloodRequestMapper.toBloodRequestEntity(bloodRequestDto, profile)
        );
        return BloodRequestMapper.toBloodRequestDto(bloodRequest);
    }

    @Override
    public BloodRequestDto fulfillBloodRequest(BloodRequestDto bloodRequestDto) {
        bloodRequestDto.setStatus(BloodRequestStatus.FULFILLED);
        bloodRequestQueue.remove(bloodRequestDto);
        pendingRequestQueue.remove(bloodRequestDto);
        
        // Get the profile for the blood request
        Profile profile = profileRepository.findById(bloodRequestDto.getProfileId())
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with ID: " + bloodRequestDto.getProfileId()));
        
        BloodRequest bloodRequest = bloodRequestRepository.save(BloodRequestMapper.toBloodRequestEntity(bloodRequestDto, profile));
        return BloodRequestMapper.toBloodRequestDto(bloodRequest);
    }

    @Override
    public List<BloodRequestDto> getEmergencyBloodRequest() {
        return bloodRequestRepository.findAllByUrgency(Urgency.HIGH)
                .stream()
                .filter(request -> request.getStatus() == BloodRequestStatus.PENDING)
                .map(BloodRequestMapper::toBloodRequestDto)
                .toList();
    }

    @PostConstruct
    @Transactional
    public void availableQueueWorker() {
        Thread worker = new Thread(() -> {
            while (true) {
                try {
                    BloodRequestDto request = bloodRequestQueue.peek();
                    if (request == null) {
                        Thread.sleep(1* 1000);
                        continue;
                    }
                    int delay = getDelayMinutes(request.getUrgency());
                    long waited = Duration.between(request.getCreatedTime(), LocalDateTime.now()).toMinutes();
                    if (waited >= delay) {
                        bloodRequestQueue.poll();
                        fulfillBloodRequest(request.getId());
                    } else {
                        // Need to fix this
                        Thread.sleep(1000 * Math.max(1, delay - waited));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    @PostConstruct
    @Transactional
    public void pendingQueueWorker() {
        Thread worker = new Thread(() -> {
            while (true) {
                try {
                    List<BloodRequestDto> toProcess = new ArrayList<>();
                    List<BloodRequestDto> pendingList = new ArrayList<>(pendingRequestQueue);

                    for (BloodRequestDto request : pendingList) {
                        List<ComponentType> componentTypes = request.getComponentRequests()
                                .stream().map(ComponentRequestDto::getComponentType).collect(Collectors.toList());
                        List<MedicalFacilityStockDto> stockDtos = medicalFacilityStockService
                                .getAvailableBloodByType(request.getBloodType(), componentTypes);

                        boolean isStockAvailable = true;
                        for (ComponentRequestDto compReq : request.getComponentRequests()) {
                            MedicalFacilityStockDto stock = stockDtos.stream()
                                    .filter(s -> s.getComponentType() == compReq.getComponentType())
                                    .findFirst().orElse(null);
                            if (stock == null || stock.getVolume() < compReq.getVolume()) {
                                isStockAvailable = false;
                                break;
                            }
                        }
                        if(request.getUrgency().equals(Urgency.HIGH) && !isStockAvailable) {
                            handleHighPriorityRequest(request, stockDtos, request.calculatePriority());
                            continue;
                        }
                        if (isStockAvailable) {
                            medicalFacilityStockService.withdrawBloodFromStock(request);
                            toProcess.add(request);
                        }
                    }

                    for (BloodRequestDto request : toProcess) {
                        if (pendingRequestQueue.remove(request)) {
                            processPendingRequest(request.getId());
                        }
                    }
                    Thread.sleep(60 * 1000); // Sleep for 1 minutes
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    private int getDelayMinutes(Urgency urgency) {
        switch (urgency) {
            case LOW:
                return LOW_URGENCY_DELAY;
            case MEDIUM:
                return MEDIUM_URGENCY_DELAY;
            case HIGH:
                return HIGH_URGENCY_DELAY;
            default:
                throw new IllegalArgumentException("Unknown urgency");
        }
    }

    @Transactional
    public void fulfillBloodRequest(Long requestId) {
        BloodRequest bloodRequest = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("BloodRequest not found"));
        bloodRequest.setStatus(BloodRequestStatus.FULFILLED);
        bloodRequestRepository.save(bloodRequest);
    }

    @Transactional
    public void processPendingRequest(Long requestId) {
        BloodRequest bloodRequest = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("BloodRequest not found"));
        bloodRequest.setStatus(BloodRequestStatus.PROCESSING);
        bloodRequestRepository.save(bloodRequest);
        bloodRequestQueue.add(BloodRequestMapper.toBloodRequestDto(bloodRequest));
    }

    @Transactional
    public void updateBloodRequestStatus(Long requestId, BloodRequestStatus status) {
        BloodRequest bloodRequest = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("BloodRequest not found"));
        bloodRequest.setStatus(status);
        bloodRequestRepository.save(bloodRequest);
    }

    @Transactional
    public BloodRequestDto handleHighPriorityRequest(BloodRequestDto bloodRequestDto, List<MedicalFacilityStockDto> stockDtos,
                                                     int newPriority) {
        List<BloodRequestDto> lowerPriorityRequests = bloodRequestQueue.stream()
                .filter(r -> r.calculatePriority() < newPriority)
                .collect(Collectors.toList());

        for (ComponentRequestDto compReq : bloodRequestDto.getComponentRequests()) {
            double reserved = lowerPriorityRequests.stream()
                    .flatMap(r -> r.getComponentRequests().stream())
                    .filter(r -> r.getComponentType() == compReq.getComponentType())
                    .mapToDouble(ComponentRequestDto::getVolume)
                    .sum();

            MedicalFacilityStockDto stock = stockDtos.stream()
                    .filter(s -> s.getComponentType() == compReq.getComponentType())
                    .findFirst().orElse(null);

            double available = (stock != null ? stock.getVolume() : 0) + reserved;
            if (available < compReq.getVolume()) {
                if (!pendingRequestQueue.contains(bloodRequestDto)){
                    bloodRequestDto.setStatus(BloodRequestStatus.PENDING);
                    pendingRequestQueue.add(bloodRequestDto);
                    updateBloodRequestStatus(bloodRequestDto.getId(), BloodRequestStatus.PENDING);
                    return bloodRequestDto;
                }
            return bloodRequestDto;
            }
        }

        // Move lower-priority requests to pending and return their reserved blood to stock
        for (BloodRequestDto lower : lowerPriorityRequests) {
            if (bloodRequestQueue.remove(lower)) {
                medicalFacilityStockService.addToStock(lower); // Return reserved blood
                lower.setStatus(BloodRequestStatus.PENDING);
                pendingRequestQueue.add(lower);
                updateBloodRequestStatus(lower.getId(), BloodRequestStatus.PENDING);
            }
        }

        medicalFacilityStockService.withdrawBloodFromStock(bloodRequestDto);
        bloodRequestDto.setStatus(BloodRequestStatus.PROCESSING);
        bloodRequestQueue.add(bloodRequestDto);
        updateBloodRequestStatus(bloodRequestDto.getId(), BloodRequestStatus.PROCESSING);
        return bloodRequestDto;
    }
}