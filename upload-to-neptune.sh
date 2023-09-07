#!/bin/sh
echo "* * * * * * * * * * * * *"
echo "Neptune Endpoint: " $1
echo "IAM role arn: " $2
echo "bucket: " $3
echo "folder name:" $4
echo "Running curl command.."
echo "* * * * * * * * * * * * * * * * * * * * * * * * * *" 
echo The URL of the curl command is https://$1:8182/loader
curl -v -H 'Content-Type: application/json' https://$1:8182/loader -d '
    {
      "source" : "s3://'$3'/'$4'",
      "format" : "turtle",
      "iamRoleArn" : "'$2'",
      "region" : "ca-central-1",
      "failOnError" : "FALSE",
      "parallelism" : "MEDIUM",
      "updateSingleCardinalityProperties" : "FALSE",
      "queueRequest" : "TRUE"
    }'
    
