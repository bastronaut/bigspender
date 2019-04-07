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