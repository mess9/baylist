## бот для общения с тудуист с помощью телеграм

https://t.me/buylistFABot

этот бот написан филом, что бы отправлять ему список покупок

основная идея такова. что бы можно было писать боту в телеграме и он бы добавлял задачи в todoist

автоматизация пользовательского опыта, раньше я накидывал в избранное в тг список дел и покупок перед выходом из дома,
чтобы ничего не забыть. но это имеет свои минусы, например нельзя отмечать уже сделанное/купленное, дабы оно не мозолило
глаз,
и приходится распределять задачи по категориям/местам вручную, или придётся видеть перед собой плоский, не
структурированный список.

бот умеет принимать в себя список дел/покупок, разбивать их по категориям и отправлять в todoist.

категории и то, что в них попадает, полностью настраивается пользователем.

## фронт будет когда-нибудь потом, вместо или как аналог тудуиста

предполагаемый адрес фронта:
`http://localhost:8080`

# планы на будущее

- доработки ux
  - [x] добавить возможность добавлять задачи в todoist
  - [x] добавить возможность добавлять задачи в todoist с разбиением по категориям
  - [x] возможность просматривать словарик категорий и вариантов
  - [x] добавить возможность просматривать список задач
  - [x] возможность добавлять новые категории
  - [x] возможность добавлять новые варианты в категории
  - [x] возможность удалять категории
  - [x] возможность удалять варианты в категориях
  - [x] возможность переименовывать категории
  - [x] добавить меню
  - [x] добавить хелп
  - [x] автоматическое перемещение внекатегорийных задач по категориям при добавлении их в словарик
  - [ ] настроить варианты ввода(в столбик, через запятую, и т.п.)
  - [ ] пустые категории сдвигать вниз
  - [ ] работа бота в чатах
  - [ ] работа бота в inline режиме
  - [ ] добавить локализацию (русский/английский)
  - [ ] редактирование словарика на фронте
  - [x] первоначальная настройка при регистрации юзера

  - дедлайны и нотификации
  - [ ] добавить возможность указывать дедлайны задачам (как сделать удобно?)
    - [ ] добавить нотификации о дедлайнах
    - [ ] добавить возможность настраивать нотификации о том что есть незавершённые задачи без дедлайнов
  - [ ] нотификации о взаимодействиях с друзьями
  - [ ] нотификации о том что друг добавил задачу
  - [ ] нотификации о том что друг выполнил задачу
  - [ ] нотификации о том что друг удалил задачу
  - [ ] нотификации о том что вас добавили в друзья

  - друзья
    - [x] многопользовательский режим
    - [x] добавление задач друзьям
    - [x] просмотр задач друзей
    - [x] добавление друзей
    - [x] удаление своих друзей
    - [x] удаление себя из друзей
    - [x] просмотр списка друзей
    - [x] доработать очистку списка задач
    - [x] доработать взаимодействие со словариком

  - игры
    - [ ] добавить пятнашки
    - [ ] добавить крестики-нолики

  - нейронка
    - [x] прикрутить нейронку
    - [ ] отложенный вывод ответа(показать что процесс идёт)
    - [x] просмотр нейронкой всех задач пользователя
    - [x] просмотр нейронкой словарика пользователя
    - [x] редактирование нейронкой словарика пользователя
    - [ ] помощь в составлении списка дел
    - [ ] помощь в составлении списка покупок
    - [ ] помощь в составлении задач на день
    - [ ] помощь в формулировке задач
    - [ ] умные напоминания
    - [x] добавление задач с датой


- доработки по технике
  - [x] создать бота
  - [x] добавить логирование
  - [x] подключить бота к тудуисту
  - [x] добавить докер
  - [x] развернуть бот в облаке
  - [x] добавить автоматическое развертывание
  - [x] подключить бд к боту
  - [ ] перейти на sync api todoist (для уменьшения количества запросов)
  - [x] переписать api клиент на feign
  - [ ] уйти от todoist на собственный фронт
  - [ ] реализовать отмечалку сделанных на кнопках в тг
  - [ ] возможность отлаживаться на [тестовом контуре тг](https://habr.com/ru/companies/selectel/articles/763286/)
  - [x] уменьшить количество обращений к бд (состояния, словарик)
  - [x] добавить кэширование
  - [ ] добавить мониторинг
  - [ ] добавить тесты
  - [ ] добавить документацию
  - [x] переделать менюшки на редактирование, а не отправку новой копии
  - [ ] зарефакторить переход состояний
    на [state machine ](https://docs.spring.io/spring-statemachine/docs/4.0.0/reference/index.html)

- баги
  - [x] не отображаются пустые категории
  - [ ] при добавлении невалидного токена - выводить об этом сообщение
  - [ ] если отправлять задачи не имея привязки к тудуист или друзей, нет предупреждения о том, что эта деятельность не
    имеет смысла
  - [x] исключить возможность добавить один вариант в разные категории
  - [x] исключить возможность добавления одинаковых категорий
  - [x] выравнять кнопки в меню с помощью невидимых пробелов

### если кто хочет помочь в разработке

пишите в телеграм https://t.me/mess9