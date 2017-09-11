package main.app;


public interface Constraints {
    float LICKERT_1 = 0.0f;
    float LICKERT_2 = 0.5f;
    float LICKERT_3 = 1.0f;
    float LICKERT_4 = 1.5f;
    float LICKERT_5 = 2.0f;
    float LICKERT_6 = 2.5f;
    float LICKERT_7 = 3.0f;

    float LICKERT_DETAILED_1 = 1.0f;
    float LICKERT_DETAILED_2 = 2.0f;
    float LICKERT_DETAILED_3 = 3.0f;
    float LICKERT_DETAILED_4 = 4.0f;
    float LICKERT_DETAILED_5 = 5.0f;

    String colors[] = {"#CCE6F4", "#F6D8AE", "#6BFFB8", "#FF6B35", "#F6F930", "#00FF00", "#775577", "#FF0000"};

    int NUMBER_OF_LICKERT_QUESTIONS = 50;
    int NUMBER_OF_DETAILED_QUESTIONS = 70;

    int NUMBER_OF_LICKERT_GROUPS = 5;
    int NUMBER_OF_LICKERT_DETAILED_GROUPS = 7;

    int NUMBER_OF_QUESTIONS_PER_PAGE = 10;

    String STRATEGIC_DESCRIPTION = "Strategia działalności przedsiębiorstwa innowacyjnego wspiera wytwarzanie innowacji. Pracownicy, kierownictwo i zarząd znają i rozumieją strategie innowacyjności przedsiębiorstwa, ponadto wspierają ją. Zdają sobie sprawę z tego, co wyróżnia przedsiębiorstwo pośród innych i daje mu przewagę konkurencyjną. Istnieją procedury wspomagające monitorowanie i zrozumienie wydarzeń rynkowych i technologicznych na strategie przedsiębiorstwa.";
    String PROCESSES_DESCRIPTION = "W przedsiębiorstwie innowacyjnym sprawnie działają mechanizmy zarządzania projektami, w tym projektami innowacyjnymi. Istnieją procesy wspierające prace rozwojowe nad nowymi produktami we wszystkich ich etapach i poszukiwanie nowych pomysłów. Projekty innowacyjne prowadzone są zgodnie z ustalonym harmonogramem czasowym i planem finansowym. W firmie sprawnie funkcjonują procesy odpowiadające za zwiększanie u pracowników świadomości potrzeb klientów.";
    String ORGANIZATION_DESCRIPTION = "W przedsiębiorstwie innowacyjnym poprawnie działa współpraca i komunikacja pomiędzy pracownikami, pionami i komórkami firmy. Struktury organizacyjne wspierają innowacyjność, m.in. poprzez systemy motywacyjno-premiowe, zachęcanie pracowników do własnych pomysłów oraz umożliwianie pracownikom realizację nowych pomysłów. Dzięki organizacji przedsiębiorstwa pracownicy aktywnie zgłaszają pomysły na udoskonalenie istniejących procesów i produktów.";
    String COUPLINGS_DESCRIPTION = "Przedsiębiorstwo innowacyjne cechuje umiejętność trafnego rozpoznawania potrzeb klientów, a także aktywna współpraca z nim i użytkownikami prowadzącymi. Ponadto przedsiębiorstwo ma bardzo dobre stosunki z uczelniami, dzięki czemu mogą pogłębiać swoją wiedzę lub zgłaszać ewentualne zapotrzebowanie na specjalistów. Pożądane są bardzo dobre relacje z ośrodkami badawczo-naukowymi, a także budowanie zewnętrznej sieci powiązań – z dostawcami, z specjalistami w swojej dziedzinie i innymi przedsiębiorstwami.";
    String LEARNING_DESCRIPTION = "Przedsiębiorstwo innowacyjne analizuje ukończone projekty, wysnuwa wnioski ze swoich doświadczeń i uczy się na własnych błędach. Analizuje swoje doświadczenia także poprzez porównywanie swoich osiągnięć z konkurencyjnymi. Ponadto chętnie korzysta z metryk usprawniających zarządzanie innowacjami. Sprzyja rozwojowi osobistemu pracowników, a także podnoszeniu ich kompetencji. Organizuje i opłaca szkolenia pracowników i zachęca do egzaminów certyfikujących. Zdobytą wiedzę aktywnie wykorzystuje w swojej działalności.";

    String DETAILED_CATEGORY_RESULT_1 = "System zarządzania innowacjami w obszarze danej strefy nie funkcjonuje";
    String DETAILED_CATEGORY_RESULT_2 = "Trudno mówić o zarządzaniu innowacjami w obszarze danej strefy, choć sporadycznie wykorzystywane są pewne elementy systemu";
    String DETAILED_CATEGORY_RESULT_3 = "W obszarze danej strefy system zarządzania innowacjami funkcjonuje częściowo";
    String DETAILED_CATEGORY_RESULT_4 = "W obszarze danej strefy system zarządzania innowacjami funkcjonuje, ale pozostają pewne możliwości poprawy jego skuteczności i efektywności";
    String DETAILED_CATEGORY_RESULT_5 = "W obszarze danej strefy system zarządzania innowacjami funkcjonuje wzorcowo";

    String DETAILED_RESULT_1 = "System zarządzania innowacjami nie funkcjonuje";
    String DETAILED_RESULT_2 = "Trudno mówić o zarządzaniu innowacjami w przedsiębiorstwie, choć sporadycznie są wykorzystywane pewne elementy systemu";
    String DETAILED_RESULT_3 = "System zarządzania innowacjami funkcjonuje częściowo";
    String DETAILED_RESULT_4 = "System zarządzania innowacjami funkcjonuje, ale pozostają pewne możliwości poprawy jego skuteczności i efektywności";
    String DETAILED_RESULT_5 = "System zarządzania innowacjami w przedsiębiorstwie funkcjonuje wzorcowo";

    int DETAILED_CAT_1_LIMIT = 15;
    int DETAILED_CAT_2_LIMIT = 25;
    int DETAILED_CAT_3_LIMIT = 35;
    int DETAILED_CAT_4_LIMIT = 45;
    int DETAILED_CAT_5_LIMIT = 50;

    int DETAILED_1_LIMIT = 105;
    int DETAILED_2_LIMIT = 175;
    int DETAILED_3_LIMIT = 245;
    int DETAILED_4_LIMIT = 315;
    int DETAILED_5_LIMIT = 350;

    int INNOVATION_QUESTION_FIRST = 18;
}
