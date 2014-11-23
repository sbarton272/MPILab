#!/bin/sh

#Number of Points
b=10

#Number of Cluster
k=2


		echo ********GENERATING $b INPUT POINTS EACH IN $k CLUSTERS 
		python ./randomclustergen/generaterawdata.py -c $k  -p $b -o input/ptCluster.csv

        echo ********GENERATING $b DNA STRANDS EACH IN $k CLUSTERS 
        python ./randomclustergen/generateDnaData.py -c $k -p $b -l 20 -o input/dnaCluster.csv
