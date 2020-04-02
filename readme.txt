# to build and test by maven 3.x with java 1.7
#
#JAVA_HOME=d:\java\jdk17
#M2_HOME=d:\apache-maven-3x
#MAVEN_OPTS=-Xms112m -Xmx112m -Dfile.encoding=UTF-8
#PATH=%PATH%;%JAVA_HOME%\bin;%JAVA_HOME%\bin
#
mvn -U clean package

# to run
cd target
java -jar ColaTest.jar


Zadanie:

Zaimplementuj aplikację konsolową Java naśladującą obsługę płatności w 
automacie z Colą. Automat obsługuje tylko sprzedaż jednego produktu, tzn. 
każda puszka Coli jest w tej samej cenie.

Inicjalnie automat posiada następujące banknoty i monety:
*  100 zł - 1 szt
*  50 zł - 3 szt
*  20 zł - 4 szt
*  10 zł - 5 szt
*  5 zł - 5 szt
*  2 zł - 10 szt
*  1 zł - 10 szt

Wersja mimimalna:

Funkcjonalność zaimplementuj w taki sposób aby:
- automat wyświetlał informację o cenie jednej puszki Coli,
- prosił o wprowadzenie listy banknotów / monet w ramach zapłaty za jedną 
puszkę Coli,
- automat wydawał określoną kwotę reszty przy użyciu minimalnej liczby 
banknotów i monet,
- w przypadku, gdy nie jest możliwe wydanie reszty wyświetl odpowiedni komunikat


Funkcjonalności opcjonalne:

Jeśli starczy Ci czasu, możesz dodatkowo:
- przenieść do konfiguracji stan automatu (listę nominałów i ich ilości)
- uwzględnić fakt, że nominały, którymi klient płaci trafiają do puli 
banknotów / monet używanych do wydawania reszty w automacie
- zapisywać stan automatu, aby przy kolejnym uruchomieniu aplikacji pobierana 
była ostatnio zgromadzona w automacie lista nominałów