package cs209a.finalproject_demo.DataAnalysis.Service;

import cs209a.finalproject_demo.DataAnalysis.Repository.TagCooccurrenceRepository;
import cs209a.finalproject_demo.DataAnalysis.dto.TagPairCount;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TagCooccurrenceService {

    private final TagCooccurrenceRepository repository;

    public TagCooccurrenceService(TagCooccurrenceRepository repository) {
        this.repository = repository;
    }

    public List<TagPairCount> getTopPairs(
            LocalDate start, LocalDate end, int topN) {
        return repository.topTagPairs(start, end, topN);
    }
}
