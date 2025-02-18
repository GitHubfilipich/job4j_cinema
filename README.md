# job4j_cinema
Сайт по покупке билетов в кинотеатр.
Позволяет просмотреть список фильмов, расписание сеансов, приобрести билет.
Покупка билетов доступна только для зарегистрированных пользователей.
Если на выбранные сеанс и место билет уже продан, то продажа билета не производится. 

# Стек технологий:  
Java 19  
PostgreSQL 16.2  
Spring Boot 2.7.6  
Maven 3.9.6  
Liquibase Maven Plugin 4.15.0  
JaCoCo Maven Plugin 0.8.8  
SQL2O 1.6.0

# Требования к окружению
Microsoft Windows 11  
Java 19  
PostgreSQL 16

# Запуск проекта:
Создать локальную копию проекта клонированием из репозитория https://github.com/GitHubfilipich/job4j_cinema  
В PostgreSQL создать базу данных и в папке проекта в файлах "...\db\liquibase.properties" и 
"...\src\main\resources\application.properties" указать её адрес (url), имя пользователя (username) и 
пароль (password).  
В папке проекта создать папку с файлами фотографий для описаний фильмов и в соответствии с ними заполнить 
скрипт заполнения таблицы файлов фотографий в "...\db\scripts\002_dml_insert_files.sql".    
Заполнить скрипты заполнения таблицы жанров (в файле "...\db\scripts\003_dml_insert_genres.sql"), фильмов 
("...\db\scripts\004_dml_insert_films.sql"), залов ("...\db\scripts\005_dml_insert_halls.sql") и сеансов 
("...\db\scripts\006_dml_insert_film_sessions.sql").  
В терминале в папке проекта выполнить скрипты создания БД и заполнения таблиц командой 
"mvn liquibase:update -Pproduction".  
Создать исполняемый файл проекта "job4j_cinema-1.0-SNAPSHOT.jar" в папке "target" проекта командой 
"mvn clean package -Pproduction -DskipTests".  
Запустить исполняемый файл командой "java -jar target/job4j_cinema-1.0-SNAPSHOT.jar".  
Сайт проекта находится по адресу http://localhost:8080/

# Взаимодействие с приложением:

Главная страница
![screen_main.png](img/screen_main.png)

Расписание
![screen_sessions.png](img/screen_sessions.png)

Кинотека
![screen_films.png](img/screen_films.png)

Страница описания фильма
![screen_film_description.png](img/screen_film_description.png)

Страница покупки билета
![screen_buy_ticket.png](img/screen_buy_ticket.png)

Страница с результатом успешной покупки билета
![screen_buy_ticket_ok.png](img/screen_buy_ticket_ok.png)

Страница с результатом неудачной покупки билета
![screen_buy_ticket_error.png](img/screen_buy_ticket_error.png)

Страница регистрации
![screen_registration.png](img/screen_registration.png)

Страница аутентификации
![screen_login.png](img/screen_login.png)

# Контакты
https://github.com/GitHubfilipich