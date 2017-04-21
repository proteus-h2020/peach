
# Proteus Peach
The peach (Prunus persica) is a deciduous tree native to the region of Northwest China between the Tarim Basin and the 
north slopes of the Kunlun Shan mountains, where it was first domesticated and cultivated. It bears an edible juicy 
fruit called a peach or a nectarine. The specific epithet persica refers to its widespread cultivation in Persia, whence
it was transplanted to Europe. It  belongs to the genus Prunus which includes the cherry, apricot, almond and plum, in 
the rose family. The peach is classified with the almond in the subgenus Amygdalus, distinguished from the other 
subgenera by the corrugated seed shell. Peach and nectarines are the same species, even though they are regarded 
commercially as different fruits. In contrast to peaches, whose fruits present the characteristic fuzz on the skin, 
nectarines are characterized by the absence of fruit-skin trichomes (fuzz-less fruit); genetic studies suggest 
nectarines are produced due to a recessive allele, whereas peaches are produced from a dominant allele for fuzzy skin. 
The People's Republic of China is the world's largest producer of peaches. – 
[Wikipedia](https://en.wikipedia.org/wiki/Peach)

#### Master branch
 
[![Build Status](https://travis-ci.org/proteus-h2020/peach.svg?branch=master)](https://travis-ci.org/proteus-h2020/peach)
 
[![codecov](https://codecov.io/gh/proteus-h2020/peach/branch/master/graph/badge.svg)](https://codecov.io/gh/proteus-h2020/peach)

#### Develop branch
 
[![Build Status](https://travis-ci.org/proteus-h2020/peach.svg?branch=develop)](https://travis-ci.org/proteus-h2020/peach)
 
[![codecov](https://codecov.io/gh/proteus-h2020/peach/branch/develop/graph/badge.svg)](https://codecov.io/gh/proteus-h2020/peach)

## What does this module do

Peach (Proteus Elastic Cache) is a distributed key-value cache that can be used both inside and outside of the 
Proteus scope. The cache aims to provide low latency responses on a distributed elastic deployment with fault-tolerance 
capabilities. As a generic design, the cache could be integrated within [Apache Flink](https://flink.apache.org/) to 
speedup computing processes.

## Using Peach

Compile the project to obtain the executable scripts:

```
$ mvn install -DskipTests
```

Move to the target directory to launch the PeachServer:

```
$ cd peach-redis-server-dist/target/peach-redis-server-dist-*/
```

Launch Peach with the Redis backend. Notice that Redis must be up and
running:

```
$ ./bin/peach-redis-server-app
```

### Launching redis in a test environment

Download [Redis](https://redis.io/download) and compile the project:

```
$ wget http://download.redis.io/releases/redis-<your_version>.tar.gz
$ tar xzf redis-<your_version>.tar.gz
$ cd redis-<your_version>
$ make
```

Once the project is successfully built, launch the server:

```
$ ./src/redis-server
```

# What is PROTEUS
PROTEUS is an EU H2020 funded research project to evolve massive online machine learning strategies for predictive 
analytics and real-time interactive visualization methods – in terms of scalability, usability and effectiveness 
dealing with extremely large data sets and data streams – into ready to use solutions, and to integrate them into 
enhanced version of Apache Flink, the EU Big Data platform. PROTEUS project is being carried out by an international 
consortium of 6 partners including Novelti (startup focused on streaming analytics), DFKI (part of the team creator 
of Apache Flink), ArcelorMitall (worlds’s leading steel company), Treelogic (creators of Lambdoop) and 
Trilateral (policy and regulatory advice on new technologies)

Official website: [PROTEUS H2020](http://www.proteus-bigdata.com/) 
Official Twitter account: [@proteus_bigdata](https://twitter.com/proteus_bigdata)
