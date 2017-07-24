package main.app;

import main.app.enums.*;
import main.app.orm.*;
import main.app.orm.methods.AuditMethods;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by Darek on 2017-03-05.
 */
public class Common {
    private static final float MAX_POINTS = (Constraints.NUMBER_OF_LICKERT_QUESTIONS * Constraints.LICKERT_7);
    private static final float MAX_POINTS_PER_LICKERT_GROUP = (Constraints.NUMBER_OF_LICKERT_QUESTIONS * Constraints.LICKERT_7) /
            Constraints.NUMBER_OF_LICKERT_GROUPS;

    private static final float MAX_POINTS_DETAILED = (Constraints.NUMBER_OF_DETAILED_QUESTIONS * Constraints.LICKERT_DETAILED_5);
    private static final float MAX_POINTS_PER_LICKERT_DETAILED_GROUP = (Constraints.NUMBER_OF_DETAILED_QUESTIONS * Constraints.LICKERT_DETAILED_5) /
            Constraints.NUMBER_OF_LICKERT_DETAILED_GROUPS;

    public static boolean isSessionActive(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    public static float getResultFromAnswers(List<Answer> answers) {
        float result = getResultOfAnswersType(answers, MAX_POINTS, true);
        if (answers.get(0).getQuestion().getType().equals(QuestionType.DETAILED)) {
            result = getResultOfAnswersType(answers, MAX_POINTS_DETAILED, true);
        }
        return result;
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

    public static float getResultFromAnswersForDetailedLickert(List<Answer> answers, QuestionCategory category, boolean percent) {
        List<Answer> newList = new ArrayList<>();
        for (Answer a : answers) {
            if (a.getQuestion().getCategory().equals(category)) {
                newList.add(a);
            }
        }
        return getResultOfAnswersType(newList, MAX_POINTS_PER_LICKERT_DETAILED_GROUP, percent);
    }

    public static List<Audit> getAuditOfType(List<Audit> allAudits, AuditType type) {
        List<Audit> newList = new ArrayList<>();

        for (Audit a : allAudits) {
            if (a.getType().equals(type)) {
                newList.add(a);
            }
        }

        return newList;
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

    private static SwotResult getCategoryResultOfRelation(SwotRelations relation) {
        SwotResult category = SwotResult.MINI_MINI;
        SwotCategory cat1 = relation.getRelationPartner1().getCategory();
        SwotCategory cat2 = relation.getRelationPartner2().getCategory();

        if (cat1.equals(SwotCategory.OPPORTUNITES)) {
            if (cat2.equals(SwotCategory.STRENGHTS)) {
                category = SwotResult.MAXI_MAXI;
            } else if (cat2.equals(SwotCategory.WEAKNESSES)) {
                category = SwotResult.MINI_MAXI;
            }
        }

        if (cat2.equals(SwotCategory.OPPORTUNITES)) {
            if (cat1.equals(SwotCategory.STRENGHTS)) {
                category = SwotResult.MAXI_MAXI;
            } else if (cat1.equals(SwotCategory.WEAKNESSES)) {
                category = SwotResult.MINI_MAXI;
            }
        }

        if (cat1.equals(SwotCategory.THREATS)) {
            if (cat2.equals(SwotCategory.STRENGHTS)) {
                category = SwotResult.MAXI_MINI;
            } else if (cat2.equals(SwotCategory.WEAKNESSES)) {
                category = SwotResult.MINI_MINI;
            }
        }

        if (cat2.equals(SwotCategory.THREATS)) {
            if (cat1.equals(SwotCategory.STRENGHTS)) {
                category = SwotResult.MAXI_MINI;
            } else if (cat1.equals(SwotCategory.WEAKNESSES)) {
                category = SwotResult.MINI_MINI;
            }
        }

        return category;
    }

    public static SwotResult getSwotResultDescription(Swot swot) {
        Map<SwotResult, Integer> resultMap = new HashMap<>();
        resultMap.put(SwotResult.MAXI_MAXI, 0);
        resultMap.put(SwotResult.MINI_MAXI, 0);
        resultMap.put(SwotResult.MAXI_MINI, 0);
        resultMap.put(SwotResult.MINI_MINI, 0);


        for (SwotRelations relation : swot.getRelations()) {
            resultMap.put(Common.getCategoryResultOfRelation(relation),
                    resultMap.get(Common.getCategoryResultOfRelation(relation)) + relation.getRelation());
        }

        // old version
        /*for (SwotCategory cat : SwotCategory.values()) {
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
        }*/
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

    public static List<Swot> getSwotsBetweendDates(List<Swot> allAudits, Date start, Date end) {
        List<Swot> swots = new ArrayList<>();

        for (Swot swot : allAudits) {
            if (swot.getSwotDate().compareTo(start) >= 0 && swot.getSwotDate().compareTo(end) <= 0) {
                swots.add(swot);
            }
        }
        swots.sort(Comparator.comparing(Swot::getSwotDate));
        return swots;
    }

    public static List<Source> getNotSelectedSource(List<Source> selectedSources) {
        List<Source> notSelectedSources = new ArrayList<>();
        List<Source> allSources = AuditMethods.getSources();
        for (Source src : allSources) {
            if (!selectedSources.contains(src)) {
                notSelectedSources.add(src);
            }
        }

        return notSelectedSources;
    }

    public static Audit getLastAuditOfType(List<Audit> audits, AuditType type) {
        Audit audit = null;
        List<Audit> auditOfType = getAuditOfType(audits, type);
        if (!auditOfType.isEmpty()) {
            audit = auditOfType.get(auditOfType.size() - 1);
        }
        return audit;
    }

    public static String stripPolishCharacters(String string) {
        char[] polish = "ąęłćźżó".toCharArray();
        char[] nonPolish = "aelzzzo".toCharArray();
        for (int i = 0; i < polish.length; ++i) {
            string = string.replace(polish[i], nonPolish[i]);
        }

        return string;
    }

    public static Map<String, List<SwotAlternatives>> separateCategoriesOfSwot(List<SwotAlternatives> swotAlternatives) {
        Map<String, List<SwotAlternatives>> map = new HashMap<>();
        for (SwotAlternatives sw : swotAlternatives) {
            if (sw.getCategory().equals(SwotCategory.OPPORTUNITES) ||
                    sw.getCategory().equals(SwotCategory.THREATS)) {
                if (!map.containsKey("rows")) {
                    map.put("rows", new ArrayList<>());
                }
                map.get("rows").add(sw);
            } else {
                if (!map.containsKey("cols")) {
                    map.put("cols", new ArrayList<>());
                }
                map.get("cols").add(sw);
            }
        }

        return map;
    }

    public static int getValueOfRelation(SwotAlternatives sw1, SwotAlternatives sw2, List<SwotRelations> relations) {
        int value = 0;
        for (SwotRelations relation : relations) {
            if ((relation.getRelationPartner1().getId() == sw1.getId() && relation.getRelationPartner2().getId() == sw2.getId()) ||
                    (relation.getRelationPartner2().getId() == sw1.getId() && relation.getRelationPartner1().getId() == sw2.getId())) {
                value = relation.getRelation();
            }
        }
        return value;
    }

    public static List<List<Idea>> getIdeasOfEmployeesOfManager(List<Idea> ideas, User manager) {
        List<Idea> empIdeas = new ArrayList<>();
        List<Idea> nonEmpIdeas = new ArrayList<>();
        List<List<Idea>> returnValue = new ArrayList<>();
        if (!manager.getEmployees().isEmpty()) {
            for (Idea i : ideas) {
                for (User emp : manager.getEmployees()) {
                    if (emp.getId() == i.getEmployee().getId()) {
                        empIdeas.add(i);
                    } else {
                        nonEmpIdeas.add(i);
                    }
                }
            }
        }
        returnValue.add(empIdeas);
        returnValue.add(nonEmpIdeas);
        return returnValue;
    }
}
