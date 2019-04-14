#####@ResponseBody:
The @ResponseBody annotation tells a controller that the object returned is automatically serialized into JSON and passed back into the HttpResponse object.
we don’t need to annotate the @RestController-annotated controllers with the @ResponseBody annotation since it’s done by default here?


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

## Tests
extending from: AbstractTransactionalJUnit4SpringContextTests, allows transactional queries that roll back after test completes