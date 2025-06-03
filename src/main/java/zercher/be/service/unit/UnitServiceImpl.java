package zercher.be.service.unit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import zercher.be.dto.unit.UnitCreateUpdateDTO;
import zercher.be.dto.unit.UnitViewDTO;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.UnitMapper;
import zercher.be.repository.UnitRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {
    private final UnitRepository unitRepository;

    private final UnitMapper unitMapper;

    @Override
    public List<UnitViewDTO> getUnits(Pageable pageable) {
        var units = unitRepository.findAll(pageable);
        return units.getContent().stream().map(unitMapper::entityToViewDTO).collect(Collectors.toList());
    }

    @Override
    public void createUnit(UnitCreateUpdateDTO createDTO) {
        var unit = unitMapper.createDTOToUnit(createDTO);
        unitRepository.save(unit);
    }

    @Override
    public void updateUnit(Long id, UnitCreateUpdateDTO updateDTO) {
        var unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("unitWithIdNotFound"));
        unitMapper.updateUnitFromDTO(updateDTO, unit);
        unitRepository.save(unit);
    }

    @Override
    public void deleteUnit(Long id) {
        var unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("unitWithIdNotFound"));
        unitRepository.delete(unit);
    }
}
