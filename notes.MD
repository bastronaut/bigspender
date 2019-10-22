Set of personal notes made while learning the stack

###@ResponseBody:
The @ResponseBody annotation tells a controller that the object returned is automatically serialized into JSON and passed back into the HttpResponse object.
we don’t need to annotate the @RestController-annotated controllers with the @ResponseBody annotation since it’s done by default for more simple objects. More complex objects do require it (it seems) 


### layer architecture
Typically you have different layers:

A persistence layer to store data
Business layer to operate on data
A presentation layer to expose data
Typically, each layer would use its own kind of objects:

Persistence Layer: Repositories, Entities
Business Layer: Services, Domain Objects
Presentation Layer: Controllers, DTOs


## Oaht2 flow - for future
### Spring boot security
Simplified flow:
1. auth request sent fro mclietn to server using password auth grant
2. access token is returned to client (with refresh token)
3. access token is sent frmo client to server on each request
4. server responds with required protected resources

Concepts:
- authorization vs authentication
- Basic interface is ```AuthenticationManager```, responsible for providing an authentication method
- ```UserDetailsService``` is the interface related to user's information collection

### Authorization
three areas:
1. web request auth
2. method levle auth
3. domain objects instance auth

Main interface: ```AccessDecisionManager```, responsible for all 3 areas by delegating to ```AccessDecisionVoter```

```AccessDecisionVoter```: represents association between an ```Authentication``` (user identity, or "Principal"), a resource, and a collection of ```ConfigAttribute``` (rules describing how resource owner allows access to resource, e.g. through roles)


## Custom controller exception responses
- New exception class (lets call it UserNotFoundException.class)
- For custom status (by default 500 server error) Add ```@ResponseStatus(HttpStatus.<desired status>)``` annotation to exception class

Customizing error response structure from default:
- Create an error response bean (lets call it ErrorDetails.class)
- Define a @ControllerAdvice:

```
@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public final ResponseEntity<ErrorDetails> handleUserNotFoundException(StudentNotFoundException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
        request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
  }
```  
### Filters
Filters have 3 actions
- init
- doFilter - the big deal
- destroy

Spring: DelegatingFilterProxy -> FilterChainProxy -> SecurityFilterChain

FilterChainProxy:
- on each request, it iterates on SecurityFilterChain until it finds a match on the requestpath

SecurityFilterChain interface:
boolean matches(HttpServletRequest request) - matches with the security filter 
List<Fitler> getFilters() - retrieves filters associated with chain

One of the first key filters: SecurityontextPersistenceFitler
SecurityContext holds context of principal, such as user, roles, etc
retrieved from a Securty context repository. typical app: the session (the session repository)

SecurityContextHolder is a threadlocal, making the SecurityContext available to all subsequent filters and application running in same thread of execution.

SecurityContext api: 
Authentication getAuthentication();
void setAuthentication(Authentication authentication);

Request -> AuthenticationFilters -> creates an Authentication object, containing UsernamePasswordAuthenticationToken. -> then delegated to Authenticationmanager
, which returns an authenticated token through to the filter

AuthenticationManager delegates to an AuthenticationProvider

Authenticationmanager api:
Authentication authenticate(Authentication authentication) throws AuthenticationException;

Authentication Api:
boolean isAuthenticated()
Object getPrincipal();
Object getCredentails();
Collection<? extends GrantedAuthority> getAuthorities();

Authenticationprovider -> (loadUserByUsername) UserDetailsService. if found

## Tests
extending from: AbstractTransactionalJUnit4SpringContextTests, allows transactional queries that roll back after test completes

## WebMvcTest
Using this annotation will disable full auto-configuration and instead apply only configuration relevant to MVC tests (i.e. @Controller, @ControllerAdvice, @JsonComponent Filter, WebMvcConfigurer and HandlerMethodArgumentResolver beans but not @Component, @Service or @Repository beans). [...] If you are looking to load your full application configuration and use MockMVC, you should consider @SpringBootTest combined with @AutoConfigureMockMvc rather than this annotation.

Getting full security context in junit test - can set breakpoint in @Configuration file to see that the file is hit

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build(); 
                
                

## Detached entities
When entites are detached, changes to the entities are not persisted. todo figure out when detachemnt happens.
For @ManyToX relations with collections, the default fetch strategy is lazy. This means that the relation entity wont be loaded when you load the initial entity. can use fetchtype=eager, but comes with massive side effects related to nr of queries if not used wisely   


## JPA

### Derived queries JPA
You can create derived queries for fields on entities (findByXAndY), see docs
https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.sample-app.finders.strategies

Search Query Creation or Query lookup strategies

@JoinTable
The @JoinTable annotation is optional. It allows you to define the name of the join table and foreign key columns that store the many-to-many association.

	@JoinTable(name = "book_author",
		joinColumns = { @JoinColumn(name = "fk_book") },
		inverseJoinColumns = { @JoinColumn(name = "fk_author") })

@ManyToMany
you probably want to navigate in both directions (see transactions by label, see labels by transaction). In that case, we need to set the @ManyToMany on both classes, and define who owns the relationship (one >NOT< having the mappedBy attribute). We need custom logic to ensure both entities are updated when an add/remove is performed

### Propery expressions
JPA can traverse fields in the repository query names. Lets say an entity has a field, and that field has a subfield. You can use findByFieldSubfield to find it. Can also be used to find an item in a list  
docs:  https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-property-expressions

### Transactional
No EntityManager with actual transaction available for current thread

https://stackoverflow.com/questions/32269192/spring-no-entitymanager-with-actual-transaction-available-for-current-thread
"checking the spring documentation it looks like by default the PersistenceContext is of type Transaction, so that's why the method has to be transactional (http://docs.spring.io/spring/docs/current/spring-framework-reference/html/orm.html):"


### Breakpoints for controllers
For easy debugging, set breakpoint in DispatcherServlet.java line 1055 for method processDispatchResult(...) in method doDispatch(...)


### Authentication and authorization
DaoAuthenticationProvider.java, line 90, additionalAuthenticationChecks(..) verifies that the given password in the authorization header matches with the users 
actual password


### Controller serialization
406 instead of 200 -> probably a problem with the serialization of the object into the ResultEntity body. Probably missing a constructor on the response type, or @getter on the fields


### Event listeners
Listening to an event:
- implement the ApplicationListener<EVENTNAME> class
- implement the method onApplicationEvent

Publishing an event:
- Autowire the ApplicationEventPublisher (controller injection)
- Invoke the publishEvent with your own event

private final ApplicationEventPublisher publisher;
...
this.publisher.publishEvent(new OrderCreatedEvent(order))

docs: https://spring.io/blog/2015/02/11/better-application-events-in-spring-framework-4-2#annotation-driven-event-listener


### JPQL queries
- JPQL query wants you to use the Entity name rather than the table name
- For non-select queries, requires a @Modifying annotation


### features todo
tijdlijn uitgaven per dag
hoofdcategorien, inzoomen  per subcategorie

### TODO improvements
- Transactions have a FetchType.EAGER on the List<Labels>. This will for every Transaction probably do another db call to retrieve all labels. More efficient would be if we retrieve all transactions for a user, retrieve all labels for a user, (so 2 queries), and either provide the them separately with foreign keys or link them somehow without doing a query for each single transaction
- DELETE request is not yet allowed on a single transaction resource. Can only DELETE to /labels, which is used even if you intend to only delete one. Low prio
- DTOs too complex. i.e.: See if we can get a List<Long> marshalled from the [ labels: {"id": 1},{"id": 2}] type requests for LabelDeleteDTO