# How to launch

Type this command in the terminal:
```bash
scala run ./main.scala -- regions=regions.json locations=locations.json output=my_results.json
```

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
