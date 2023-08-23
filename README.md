# simple inferencer

TO BE IMPROVED.


This just creates a model in memory and throw it back in the same folder (hardcoded in the dockerfile for now)

Typical run.  The location of the ttl files should be in `/export/data` - also hardcoded in the docker file.  You either load this information in there through the dockerfile or just share a volume
 
`docker run --rm -v /c/java64_8/GSIP/WebContent/repos/gsip:/export/data  gin_inferencer`

## To compile java code (prior to generation of docker image - to be improved too)

`mvn clean`
`mvn package`

then build the docker image

`docker build -t gin_inferencer .`