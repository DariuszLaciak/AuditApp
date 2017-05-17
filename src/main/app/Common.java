package main.app;

import main.app.enums.QuestionCategory;
import main.app.enums.QuestionType;
import main.app.orm.Answer;
import main.app.orm.Audit;
import main.app.orm.Question;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Darek on 2017-03-05.
 */
public class Common {
    private static final float MAX_POINTS = (Constraints.NUMBER_OF_LICKERT_QUESTIONS * Constraints.LICKERT_7) +
            (Constraints.NUMBER_OF_YES_NO_QUESTIONS * Constraints.YES_VAL);
    private static final float MAX_POINTS_PER_LICKERT_GROUP = (Constraints.NUMBER_OF_LICKERT_QUESTIONS * Constraints.LICKERT_7) /
            Constraints.NUMBER_OF_LICKERT_GROUPS;
    private static final float MAX_POINTS_OF_YES_NO_QUESTIONS = (Constraints.NUMBER_OF_YES_NO_QUESTIONS * Constraints.YES_VAL);

    public static boolean isSessionActive(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    public static float getResultFromAnswers(List<Answer> answers) {
        return getResultOfAnswersType(answers, MAX_POINTS, true);
    }

    public static float getResultFromAnswersForLickert(List<Answer> answers, QuestionCategory category, boolean percent) {
        List<Answer> newList = new ArrayList<>();
        for (Answer a : answers) {
            if (a.getQuestion().getCategory().equals(category)) {
                newList.add(a);
            }
        }
        return getResultOfAnswersType(newList, MAX_POINTS_PER_LICKERT_GROUP, percent);
    }

    public static float getResultFromAnswersForYesNo(List<Answer> answers) {
        List<Answer> newList = new ArrayList<>();
        for (Answer a : answers) {
            if (a.getQuestion().getType().equals(QuestionType.YES_NO)) {
                newList.add(a);
            }
        }
        return getResultOfAnswersType(newList, MAX_POINTS_OF_YES_NO_QUESTIONS, true);
    }

    private static float getResultOfAnswersType(List<Answer> answers, float max_to_calculate, boolean percent) {
        float result;
        float sumOfAnswers = 0;
        for (Answer ans : answers) {
            sumOfAnswers += ans.getAnswer();
        }
        if (percent) {
            result = sumOfAnswers / max_to_calculate;
        } else {
            result = sumOfAnswers;
        }
        return result;
    }

    public static List<Question> getRandomQuestionsAndRemoveAskedFromSession(List<Question> questions, HttpSession session) {
        List<Question> randomQuestions = new ArrayList<>();
        Random rand = new Random(System.currentTimeMillis());
        int numberOfIterations = Constraints.NUMBER_OF_QUESTIONS_PER_PAGE;
        if (questions.size() < numberOfIterations) {
            numberOfIterations = questions.size();
        }
        for (int i = 0; i < numberOfIterations; ++i) {
            randomQuestions.add(questions.remove(rand.nextInt(questions.size())));
        }
        session.setAttribute("notAskedQuestions", questions);
        return randomQuestions;
    }

    public static Audit getAudit(List<Audit> audits, long id) {
        for (Audit a : audits) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }
}
