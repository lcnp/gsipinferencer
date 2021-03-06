FROM java:8-jdk-alpine
MAINTAINER eric.boisvert2@canada.ca
COPY ./target/GinInfer.jar /usr/app/GinInfer.jar 
COPY upload-to-neptune.sh /usr/app/upload-to-neptune.sh
RUN apk --no-cache add curl
RUN ["chmod", "+x","/usr/app/upload-to-neptune.sh"]
CMD ["sh","-c","java -jar /usr/app/GinInfer.jar -i $SourceBucketName -d $DEFAULT_INPUT_FOLDER -o $DEFAULT_OUTPUT_FOLDER ; /usr/app/upload-to-neptune.sh $NEPTUNE_ENDPOINT $IAM_ROLE_ARN $SourceBucketName $DEFAULT_OUTPUT_FOLDER"]
