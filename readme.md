Разобраться с тем, как обрабатывает запросы Spring WebFlux.
Написать стартер, который должен логировать шаги обработки входящего запроса.

0. Выбрать способ однозначной идентификации принадлежности записи в логе конкретному запросу.
1. Получен входящий запрос — залогировать его заголовки, метод, путь.
2. Отдаётся успешный ответ на запрос — залогировать заголовки, длительность обработки запроса.
3. Возникла ошибка при разборе запроса (синтаксическая ошибка в пути, в теле или любая другая ошибка, обычно отображающаяся статусом 40x) — залогировать ошибку, длительность обработки запроса.
4. Возникла ошибка в процессе обработки запроса в контроллере (любая ошибка, обычно отображающаяся статусом 50x) — залогировать ошибку, длительность обработки запроса.
5. Запрос получил клиентскую отмену — залогировать отмену, длительность обработки запроса.


tutorials & docs:
- https://stackoverflow.com/questions/51738140/how-to-correctly-use-slf4j-mdc-in-spring-webflux-webfilter/77418615#77418615
- https://stackoverflow.com/questions/66408766/implement-trace-id-with-spring-webflux
- https://stackoverflow.com/questions/76045158/log-all-response-and-request-spring-webflux
- https://www.baeldung.com/spring-webflux-filters
- https://www.baeldung.com/mdc-in-log4j-2-logback
- https://spring.io/blog/2023/03/30/context-propagation-with-project-reactor-3-unified-bridging-between-reactive
- https://www.vincenzoracca.com/en/blog/framework/spring/spring-webflux-mdc/
- https://stackoverflow.com/questions/77303677/virtual-threads-and-spring-webflux

Библиотека для скоростного создания UUIDv6, упорядоченных по времени
- https://github.com/f4b6a3/uuid-creator/wiki/1.6.-UUIDv6

не относится к делу, но тоже интересно:
- https://dev.to/aleksk1ng/spring-webflux-and-grpc-319l
- https://habr.com/ru/articles/537716/

и вообще, оказывается, есть уже готовая либа:
- https://github.com/lipiridi/webflux-logging