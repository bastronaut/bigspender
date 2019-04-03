#####@ResponseBody:
The @ResponseBody annotation tells a controller that the object returned is automatically serialized into JSON and passed back into the HttpResponse object.
we don’t need to annotate the @RestController-annotated controllers with the @ResponseBody annotation since it’s done by default here?