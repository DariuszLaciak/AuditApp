package main.app;

import main.app.enums.QuestionCategory;
import main.app.enums.QuestionType;
import main.app.orm.Answer;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Darek on 2017-03-05.
 */
public class Common {
    private static final float MAX_POINTS = (Constraints.NUMBER_OF_LICKERT_QUESTIONS*Constraints.LICKERT_7) +
                (Constraints.NUMBER_OF_YES_NO_QUESTIONS*Constraints.YES_VAL);
    private static final float MAX_POINTS_PER_LICKERT_GROUP = (Constraints.NUMBER_OF_LICKERT_QUESTIONS*Constraints.LICKERT_7)/
            Constraints.NUMBER_OF_LICKERT_GROUPS;
    private static final float MAX_POINTS_OF_YES_NO_QUESTIONS = (Constraints.NUMBER_OF_YES_NO_QUESTIONS*Constraints.YES_VAL);

    public static boolean isSessionActive(HttpSession session){
        return session.getAttribute("userId") != null;
    }

    public static float getResultFromAnswers(List<Answer> answers){
        return getResultOfAnswersType(answers,MAX_POINTS);
    }

    public static float getResultFromAnswersForLickert(List<Answer> answers, QuestionCategory category){
        List<Answer> newList = new ArrayList<>();
        for(Answer a : answers){
            if(a.getQuestion().getCategory().equals(category)){
                newList.add(a);
            }
        }
        return getResultOfAnswersType(newList,MAX_POINTS_PER_LICKERT_GROUP);
    }

    public static float getResultFromAnswersForYesNo(List<Answer> answers){
        List<Answer> newList = new ArrayList<>();
        for(Answer a : answers){
            if(a.getQuestion().getType().equals(QuestionType.YES_NO)){
                newList.add(a);
            }
        }
        return getResultOfAnswersType(newList,MAX_POINTS_OF_YES_NO_QUESTIONS);
    }

    private static float getResultOfAnswersType(List<Answer> answers, float max_to_calculate){
        float result;
        float sumOfAnswers = 0;
        for(Answer ans : answers){
            sumOfAnswers += ans.getAnswer();
        }
        result = sumOfAnswers / max_to_calculate;
        return result;
    }
}
