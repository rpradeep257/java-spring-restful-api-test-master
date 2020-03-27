# Hunter Six - Java Spring RESTful API Test

## Exercises
### Exercise 1
Make the ALL tests run green (there is one failing test)

#####Solution:

- Test _idsShouldBeDifferent_ in PersonTest was failing because the _counter_ variable was declared at instance level. The fix is to declare it at class level.

### Exercise 2
Update the existing `/person/{lastName}/{firstName}` endpoint to return an appropriate RESTful response when the requested person does not exist in the list
- prove your results

#####Solution: 
- Returning matched record or resource not found

### Exercise 3
Write a RESTful API endpoint to retrieve a list of all people with a particular surname
- pay particular attention to what should be returned when there are no match, one match, multiple matches
- prove your results

#####Solution:
 
- Returning a list for all cases with OK status
- Added a custom message in case there are no matching records
- Ideally should use pagination with count. 

### Exercise 4
Write a RESTful API endpoint to add a new value to the list
- pay attention to what should be returned when the record already exists
- pay attention to what information is supplied to the calling client
- prove your resutls

#####Solution:

- Creating a new person and returning ID 
- Could return the URL to created resource 
- Added basic validation 

### Exercise 5
Write a RESTful API endpoint to update ONLY the first name (partial update)
- pay attention to what information is supplied to the calling client
- prove your results

#####Solution:

- Basic patch implementation
- Could be replaced with a library like json-patch