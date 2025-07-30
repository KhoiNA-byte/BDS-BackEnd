package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.BloodRequestDto;
import com.blooddonation.blood_donation_support_system.dto.ComponentRequestDto;
import com.blooddonation.blood_donation_support_system.dto.MedicalFacilityStockDto;
import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.enums.*;
import com.blooddonation.blood_donation_support_system.mapper.BloodRequestMapper;
import com.blooddonation.blood_donation_support_system.mapper.MedicalFacilityStockMapper;
import com.blooddonation.blood_donation_support_system.mapper.ProfileMapper;
import com.blooddonation.blood_donation_support_system.repository.BloodRequestRepository;
import com.blooddonation.blood_donation_support_system.repository.BloodUnitRepository;
import com.blooddonation.blood_donation_support_system.repository.MedicalFacilityStockRepository;
import com.blooddonation.blood_donation_support_system.service.MedicalFacilityStockService;
import com.blooddonation.blood_donation_support_system.validator.MedicalFacilityStockValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicalFacilityStockServiceImpl implements MedicalFacilityStockService {
    @Autowired
    private MedicalFacilityStockRepository medicalFacilityStockRepository;
    @Autowired
    private BloodUnitRepository bloodUnitRepository;
    @Autowired
    private MedicalFacilityStockValidator validator;
    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @Override
    public String addBloodUnitsToStockByEventId(Long eventId, String userEmail) {
        // Fetch Data
        List<BloodUnit> bloodUnits = validator.validateAndGetBloodUnits(eventId);

        // Add Blood Units to Stock
        for (BloodUnit bloodUnit : bloodUnits) {
            MedicalFacilityStock stock = MedicalFacilityStockMapper.fromBloodUnit(bloodUnit);

            if (bloodUnit.getComponentType() == ComponentType.WHOLE_BLOOD) {
                List<MedicalFacilityStock> components = divideWholeBloodIntoComponents(stock, eventId);
                for (MedicalFacilityStock component : components) {
                    updateOrCreateStock(component);
                }
            } else {
                updateOrCreateStock(stock);
            }

            // Update Blood Unit status to COMPLETED
            bloodUnit.setStatus(BloodUnitStatus.COMPLETED);
            bloodUnitRepository.save(bloodUnit);
        }

        return String.format("Successfully added %d blood units to stock", bloodUnits.size());
    }

    @Transactional
    public int withdrawBloodFromStock(BloodRequestDto bloodRequestDto) {
        int dataChanges = 0;
        for (ComponentRequestDto componentRequest : bloodRequestDto.getComponentRequests()) {
            dataChanges += medicalFacilityStockRepository.withdrawBloodFromStock(
                    bloodRequestDto.getBloodType(),
                    componentRequest.getComponentType(),
                    componentRequest.getVolume()
            );
        }
        if(!bloodRequestDto.isAutomation()) {
            bloodRequestDto.setStatus(BloodRequestStatus.FULFILLED);
            bloodRequestRepository.save(BloodRequestMapper.toBloodRequestEntity(bloodRequestDto, ProfileMapper.toEntity(bloodRequestDto.getProfile())));
        }
        return dataChanges;
    }


    public String updateBeforeWithdraw(String userEmail) {
        List<MedicalFacilityStock> stocks = medicalFacilityStockRepository.findAll();
        StringBuilder removedStock = new StringBuilder();
        int count = 0;

        for (MedicalFacilityStock stock : stocks) {
            if (stock.getExpiryDate().isBefore(LocalDate.now())) {
                removedStock.append("Please removed expired stock at your facility: ID=")
                        .append(stock.getId())
                        .append(", Type=")
                        .append(stock.getBloodType())
                        .append(", Component=")
                        .append(stock.getComponentType())
                        .append(", Volume=")
                        .append(stock.getVolume())
                        .append(", Expiry=")
                        .append(stock.getExpiryDate())
                        .append("\n");

                medicalFacilityStockRepository.delete(stock);
                count++;
            }
        }

        if (count == 0) {
            return "No expired stocks found";
        }

        return String.format("Removed %d expired stocks:\n%s", count, removedStock.toString());
    }

    @Override
    public List<MedicalFacilityStockDto> getAllAvailableBlood() {
        List<MedicalFacilityStock> medicalFacilityStocks = medicalFacilityStockRepository.findAllAvailableBlood();
        return medicalFacilityStocks.stream()
                .map(MedicalFacilityStockMapper::toDto)
                .toList();
    }

    @Override
    public List<MedicalFacilityStockDto> getAvailableBloodByType(BloodType bloodType, List<ComponentType> componentTypes) {
        List<MedicalFacilityStock> medicalFacilityStocks = medicalFacilityStockRepository.findAvailableBloodByType(bloodType, componentTypes);
        return medicalFacilityStocks.stream()
                .map(MedicalFacilityStockMapper::toDto)
                .toList();
    }
    @Override
    public List<MedicalFacilityStockDto> getAvailableBloodByType(BloodType bloodType, ComponentType componentType) {
        List<MedicalFacilityStock> medicalFacilityStocks = medicalFacilityStockRepository.findAvailableBloodByType(bloodType, componentType);
        return medicalFacilityStocks.stream()
                .map(MedicalFacilityStockMapper::toDto)
                .toList();
    }

    private void updateOrCreateStock(MedicalFacilityStock newStock) {
        medicalFacilityStockRepository.findByBloodTypeAndComponentType(
                newStock.getBloodType(),
                newStock.getComponentType()
        ).ifPresentOrElse(
                existingStock -> {
                    existingStock.setVolume(existingStock.getVolume() + newStock.getVolume());
                    medicalFacilityStockRepository.save(existingStock);
                },
                () -> medicalFacilityStockRepository.save(newStock)
        );
    }

    @Transactional
    protected List<MedicalFacilityStock> divideWholeBloodIntoComponents(MedicalFacilityStock wholeBloodStock, Long eventId) {
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);
        LocalDate donationDate = donationEvent.getDonationDate();
        double originalVolume = wholeBloodStock.getVolume();
        BloodType bloodType = wholeBloodStock.getBloodType();

        List<MedicalFacilityStock> components = List.of(
                MedicalFacilityStockMapper.createComponent(
                        bloodType,
                        ComponentType.PLASMA,
                        (int) Math.floor(originalVolume * 0.55),
                        donationDate.plusYears(1)
                ),
                MedicalFacilityStockMapper.createComponent(
                        bloodType,
                        ComponentType.RED_BLOOD_CELLS,
                        (int) Math.floor(originalVolume * 0.44),
                        donationDate.plusDays(42)
                ),
                MedicalFacilityStockMapper.createComponent(
                        bloodType,
                        ComponentType.PLATELETS,
                        (int) Math.floor(originalVolume * 0.01),
                        donationDate.plusWeeks(1)
                )
        );

        return components;
    }
    @Transactional
    public int addToStock(BloodRequestDto bloodRequestDto) {
        int dataChanges = 0;
        for (ComponentRequestDto componentRequest : bloodRequestDto.getComponentRequests()) {
            dataChanges += medicalFacilityStockRepository.addStock(bloodRequestDto.getBloodType(),
                    componentRequest.getComponentType(),
                    componentRequest.getVolume(),
                    componentRequest.getExpiredDate());
        }
        return dataChanges;
    }
    @Override
    public MedicalFacilityStockDto addBloodIntoStock(MedicalFacilityStockDto stockDto) {
        MedicalFacilityStock entity = MedicalFacilityStockMapper.toEntity(stockDto);
        MedicalFacilityStock saved = medicalFacilityStockRepository.save(entity);
        return MedicalFacilityStockMapper.toDto(saved);
    }

    @Override
    public void deleteStockById(Long id) {
        medicalFacilityStockRepository.deleteById(id);
    }
}
