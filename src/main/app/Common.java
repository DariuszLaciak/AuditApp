package main.app;

import main.app.enums.QuestionCategory;
import main.app.enums.QuestionType;
import main.app.enums.SwotCategory;
import main.app.enums.SwotResult;
import main.app.orm.Answer;
import main.app.orm.Audit;
import main.app.orm.Question;
import main.app.orm.SwotAlternatives;

import javax.servlet.http.HttpSession;
import java.util.*;

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

    public static List<SwotAlternatives> getSwotOfCategory(List<SwotAlternatives> swot, SwotCategory cat) {
        List<SwotAlternatives> result = new ArrayList<>();
        for (SwotAlternatives sw : swot) {
            if (sw.getCategory().equals(cat)) {
                result.add(sw);
            }
        }
        return result;
    }

    public static SwotResult getSwotResultDescription(List<SwotAlternatives> swot) {
        Map<SwotResult, Integer> resultMap = new HashMap<>();
        resultMap.put(SwotResult.MAXI_MAXI, 0);
        resultMap.put(SwotResult.MINI_MAXI, 0);
        resultMap.put(SwotResult.MAXI_MINI, 0);
        resultMap.put(SwotResult.MINI_MINI, 0);

        for (SwotCategory cat : SwotCategory.values()) {
            switch (cat) {
                case OPPORTUNITES:
                    resultMap.put(SwotResult.MAXI_MAXI, resultMap.get(SwotResult.MAXI_MAXI) + Common.getSwotOfCategory(swot, cat).size());
                    resultMap.put(SwotResult.MINI_MAXI, resultMap.get(SwotResult.MINI_MAXI) + Common.getSwotOfCategory(swot, cat).size());
                    break;
                case THREATS:
                    resultMap.put(SwotResult.MAXI_MINI, resultMap.get(SwotResult.MAXI_MINI) + Common.getSwotOfCategory(swot, cat).size());
                    resultMap.put(SwotResult.MINI_MINI, resultMap.get(SwotResult.MINI_MINI) + Common.getSwotOfCategory(swot, cat).size());
                    break;
                case STRENGHTS:
                    resultMap.put(SwotResult.MAXI_MAXI, resultMap.get(SwotResult.MAXI_MAXI) + Common.getSwotOfCategory(swot, cat).size());
                    resultMap.put(SwotResult.MAXI_MINI, resultMap.get(SwotResult.MAXI_MINI) + Common.getSwotOfCategory(swot, cat).size());
                    break;
                case WEAKNESSES:
                    resultMap.put(SwotResult.MINI_MAXI, resultMap.get(SwotResult.MINI_MAXI) + Common.getSwotOfCategory(swot, cat).size());
                    resultMap.put(SwotResult.MINI_MINI, resultMap.get(SwotResult.MINI_MINI) + Common.getSwotOfCategory(swot, cat).size());
                    break;
                default:
                    break;
            }
        }
        int currentMax = 0;
        SwotResult result = SwotResult.MAXI_MAXI;
        for (Map.Entry<SwotResult, Integer> entry : resultMap.entrySet()) {
            if (entry.getValue() > currentMax) {
                result = entry.getKey();
                currentMax = entry.getValue();
            } else if (entry.getValue() == currentMax) {
                result = entry.getKey().getPriority() < result.getPriority() ? entry.getKey() : result;
            }
        }
        return result;
    }

    public static List<Audit> getAuditsBetweendDates(List<Audit> allAudits, Date start, Date end) {
        List<Audit> audits = new ArrayList<>();

        for (Audit audit : allAudits) {
            if (audit.getAuditDate().compareTo(start) >= 0 && audit.getAuditDate().compareTo(end) <= 0
                    && audit.getResult() != null) {
                audits.add(audit);
            }
        }
        audits.sort(Comparator.comparing(Audit::getAuditDate));
        return audits;
    }

    public static String stripPolishCharacters(String string) {
        char[] polish = "ąęłćźżó".toCharArray();
        char[] nonPolish = "aelzzzo".toCharArray();
        for (int i = 0; i < polish.length; ++i) {
            string = string.replace(polish[i], nonPolish[i]);
        }

        return string;
    }
}
