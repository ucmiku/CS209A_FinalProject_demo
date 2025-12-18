package cs209a.finalproject_demo.dataCollection.Service;

import cs209a.finalproject_demo.dataCollection.Attributes.Question;
import cs209a.finalproject_demo.dataCollection.Attributes.QuestionTag;
import cs209a.finalproject_demo.dataCollection.Repository.QuestionRepository;
import cs209a.finalproject_demo.dataCollection.Repository.QuestionTagRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionTagService {

    private final QuestionRepository questionRepository;
    private final QuestionTagRepository questionTagRepository;

    public QuestionTagService(QuestionRepository questionRepository,
                              QuestionTagRepository questionTagRepository) {
        this.questionRepository = questionRepository;
        this.questionTagRepository = questionTagRepository;
    }

    @Transactional
    public int buildQuestionTags() {

        List<Question> questions = questionRepository.findAll();
        List<QuestionTag> batch = new ArrayList<>(1024);

        for (Question q : questions) {
            String tags = q.getTags();
            if (tags == null || tags.isBlank()) continue;

            String[] tagArr = tags.split(",");

            for (String t : tagArr) {
                String tag = t.trim().toLowerCase();
                if (tag.isEmpty()) continue;

                batch.add(new QuestionTag(q.getQuestionId(), tag));
            }

            // 批量 flush
            if (batch.size() >= 1000) {
                questionTagRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            questionTagRepository.saveAll(batch);
        }

        return questions.size();
    }
}
