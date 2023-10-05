package pro.sky.petshelterbot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.entity.Report;
import pro.sky.petshelterbot.repository.ReportRepository;
import pro.sky.petshelterbot.util.DataGenerator;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @MockBean
    private ReportRepository reportRepository =  Mockito.mock(ReportRepository.class);

    private final ReportService reportService = new ReportService(reportRepository);

    @Test
    void findAllByShelterId() {
        int count = DataGenerator.generateCount();
        Shelter shelter = DataGenerator.generateShelter();
        Long shelterId = shelter.getId();
        List<Report> reports = Stream.generate(DataGenerator::generateReport)
                .limit(count)
                .collect(Collectors.toList());

        Page<Report> page = new PageImpl<>(reports);

        when(reportRepository.findAllByShelterId(any(Long.class), any(Pageable.class)))
                .thenReturn(page);

        Collection<Report> actual = reportService
                .getAllByShelterId(shelterId, 0, count);

        assertThat(actual)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(reports);
        assertThat(actual.size()).isEqualTo(count);
    }


    @Test
    void update() {
        Report report = DataGenerator.generateReport();

        when(reportRepository.save(report)).thenReturn(report);

        Report actual = reportService.update(report);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(report);
    }

    @Test
    void disapprove() {
        Report report = DataGenerator.generateReport();

        when(reportRepository.getById(report.getId())).thenReturn(report);
        when(reportRepository.save(report)).thenReturn(report);

        Report actual = reportService.disapprove(report.getId());

        assertThat(actual)
                .isNotNull()
                .isEqualTo(report);
        assertThat(actual.isApproved()).isFalse();
        assertThat(actual.isChecked()).isTrue();
    }

}