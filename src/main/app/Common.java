package main.app;

import main.app.enums.*;
import main.app.orm.*;
import main.app.orm.methods.AuditMethods;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    public static float getResultFromAnswers(List<Answer> answers, boolean percent) {
        float result = getResultOfAnswersType(answers, MAX_POINTS, percent);
        if (answers.get(0).getQuestion().getType().equals(QuestionType.DETAILED)) {
            result = getResultOfAnswersType(answers, MAX_POINTS_DETAILED, percent);
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

    public static List<InnovationQuestion> getQuestionOfCategory(InnovationCategory category, List<InnovationQuestion> questions) {
        List<InnovationQuestion> categoryQuestions = new ArrayList<>();
        for (InnovationQuestion question : questions) {
            if (question.getCategory().equals(category)) {
                categoryQuestions.add(question);
            }
        }
        return categoryQuestions;
    }

    public static JSONObject findAdditionalAnswer(JSONArray additional, String id) {
        for (Object jo : additional) {
            JSONObject ob = (JSONObject) jo;
            if (ob.get("id").equals(id)) {
                return ob;
            }
        }
        return null;
    }

    public static String getAuthorOfInnovation(Innovation innovation) {
        String name = innovation.getAnswers().get(0).getContent();
        return name;
    }

    public static List<InnovationAnswer> getAnswersForCategory(List<InnovationAnswer> answers, InnovationCategory category) {
        List<InnovationAnswer> categoryAnswers = new ArrayList<>();

        for (InnovationAnswer answer : answers) {
            if (answer.getQuestion().getCategory().equals(category)) {
                categoryAnswers.add(answer);
            }
        }
        return categoryAnswers;
    }

    public static String calculateResult(int result) {
        String desc;
        if (result <= Constraints.DETAILED_1_LIMIT) {
            desc = Constraints.DETAILED_RESULT_1;
        } else if (result <= Constraints.DETAILED_2_LIMIT) {
            desc = Constraints.DETAILED_RESULT_2;
        } else if (result <= Constraints.DETAILED_3_LIMIT) {
            desc = Constraints.DETAILED_RESULT_3;
        } else if (result <= Constraints.DETAILED_4_LIMIT) {
            desc = Constraints.DETAILED_RESULT_4;
        } else {
            desc = Constraints.DETAILED_RESULT_5;
        }
        return desc;
    }

    public static String calculateResultForCategory(int result) {
        String desc;
        if (result <= Constraints.DETAILED_CAT_1_LIMIT) {
            desc = Constraints.DETAILED_CATEGORY_RESULT_1;
        } else if (result <= Constraints.DETAILED_CAT_2_LIMIT) {
            desc = Constraints.DETAILED_CATEGORY_RESULT_2;
        } else if (result <= Constraints.DETAILED_CAT_3_LIMIT) {
            desc = Constraints.DETAILED_CATEGORY_RESULT_3;
        } else if (result <= Constraints.DETAILED_CAT_4_LIMIT) {
            desc = Constraints.DETAILED_CATEGORY_RESULT_4;
        } else {
            desc = Constraints.DETAILED_CATEGORY_RESULT_5;
        }
        return desc;
    }

    public static Map<String, List<String>> getCategoryOptions(InnovationCategory category) {
        Map<String, List<String>> map = null;
        List<String> list;
        switch (category) {
            case PRODUCT:
                map = new HashMap<>();
                list = new ArrayList<>();
                list.add("Nowa");
                list.add("Ulepszona");
                map.put("Usługa", list);
                list.clear();
                list.add("Nowy");
                list.add("Ulepszony");
                map.put("Produkt", list);
                break;
            case PROCESS:
                map = new HashMap<>();
                list = new ArrayList<>();
                list.add("Nowa");
                list.add("Ulepszona");
                map.put("Metoda", list);
                list.clear();
                list.add("Nowy");
                list.add("Ulepszony");
                map.put("Proces", list);
                break;
            case ORGANIZATION:
                map = new HashMap<>();
                list = new ArrayList<>();
                list.add("Metoda organizacji pracy");
                list.add("Metoda organizacji produkcji");
                list.add("Metoda organizacji relacji zewnętrznych");
                map.put("Nowa", list);
                map.put("Ulepszona", list);
                break;
            case MARKETING:
                map = new HashMap<>();
                list = new ArrayList<>();
                list.add("Metoda promocji");
                list.add("Metoda wizerunku");
                list.add("Metoda strategii cenowej");
                map.put("Nowa", list);
                map.put("Ulepszona", list);
                break;
            default:
                break;
        }
        return map;
    }

    public static Map<Integer, String> getTypeOptions(InnovationType type) {
        Map<Integer, String> map = null;
        switch (type) {
            case YES_NO:
                map = new HashMap<>();
                map.put(1, "Tak");
                map.put(0, "Nie");
                break;
            case RADIO:
                map = new HashMap<>();
                map.put(0, "Idea");
                map.put(1, "Badania podstawowe");
                map.put(2, "Badania stosowane");
                map.put(3, "Potwierdzona koncepcja");
                map.put(4, "Prototyp");
                map.put(5, "Prototyp gotowy do wdrożenia");
                break;
            case RADIO2:
                map = new HashMap<>();
                map.put(0, "Procedura nie została rozpoczęta");
                map.put(1, "Zgłoszony wniosek patentowy");
                map.put(2, "Patenty przyznane");
                break;
        }
        return map;
    }

    public static InnovationCategory getCategoryByNumber(int number) {
        for (InnovationCategory innovationCategory : InnovationCategory.values()) {
            if (innovationCategory.getNumber() == number) {
                return innovationCategory;
            }
        }
        return null;
    }

}
