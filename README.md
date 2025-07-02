# internship-task

This is a test assignment for Scala TravelTime internship.

For this test task your goal is to match locations to their appropriate regions.

- Location - coordinates of a point. Each location has a name.
- Region - a set of coordinates describing a polygon. Each polygon has a name.

You have to find which locations are within the given regions. 
A single region can contain multiple locations. A single location can appear in multiple regions, thus meaning regions can overlap with each other.
Locations and regions are provided in separate JSON files. You will have to parse these files to read locations and regions data. 

After you successfully parsed JSON files you will need to create an algorithm or use third party library that matches locations to their regions based on their coordinates.
The result of your task should be a JSON file which list all of the regions with their coresponding locations.


**Reminder**:

One region can contain multiple polygons. To display coordinates of provided regions on a map, you can use this website https://geojson.io/. 

### Input files:

[Locations](input/locations.json):
```js
[
  {
    "name": "<unique identifier>",
    "coordinates": [<longitude>, <latitude>]
  },
  ... // more locations
]
```

[Regions](input/regions.json):
```js
[
  {
    "name": "<unique identifier>",
    "coordinates": [
      [[<longitude>, <latitude>], [<longitude>, <latitude>]], 
        ... // more polygons    
    ] - array of polygons, where each polygon is an array of coordinates.
  },
  ... // more regions
]
```

### Output files:

[Results](output/results.json):
```js
[
  {
    "region": "<region identifier>",
    "matched_locations": [
      "<location identifier>",
      "<location identifier>",
    ]
  },
  ... // more regions
]
```


### Main requirements:
* Any statically typed programming language can be used for this task
* Try to implement the solution as if it was a real production level application, not just a minimal script
* Submit your solution that we could access, you should do it via a GitHub repository. If you are not writing your solution in Scala or Java, it would be convenient for us if you send an additional link to [Replit](https://replit.com/) with your solution. Replit is an online IDE and hosting platform. If you submit the code there, we can easily run it without setting up languange dependencies
* Cover all edge cases and make sure that your solution works correctly
* Create a README which describes how to run your program in the terminal. Pass region and location files as input parameters. Example: **scala your-app --regions=regions.json --locations=locations.json --output=results.json**
* Cover your solution with unit tests (including all edge cases)


### Optional requirements:
* Implement it in Scala
* Implement the algorithm which will match the location with the corresponding region

### Usage of AI for the solution

We strongly urge you to try to complete the task without relying on AI. The task is not overly complex,
so an LLM will not struggle too much to solve it, but that is not the point. The purpose of this task
is to show us your current skill level, your ability to find and solve problems and most importantly -
explain the decisions and design choices you made along the way. A less fleshed out solution
that you understand and can explain is way more valuable to us than a vibe coded one.

Even if your first submission is lacking, as long as we see that you are moving in the right direction,
we will provide some feedback and let you improve it.

Usage of AI is generally okay for simple purposes, such as tool discovery (example could be asking what
JSON parsing libraries exist in your language of choice) and other similar purposes. Just don't vibe code :)
