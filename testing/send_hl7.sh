#!/bin/bash

# Script to send test HL7 data to Health Data Receiver.

DATA_RECEIVER_PORT=1234

DATAFILE=hl7data/HL7_sample.txt # todo 3 errors to reveal
#DATAFILE=hl7data/HL7v2.4.ECG_order.txt
#DATAFILE=hl7data/HL7v2.4.ORU_1.txt # Message type not yet supported ORU_R40
#DATAFILE=hl7data/HL7v2.4.ORU_2.txt # Message type not yet supported ORU_R40
#DATAFILE=hl7data/HL7_short.txt
#DATAFILE=hl7data/HL7_2MSHs.txt

#nc -v localhost $DATA_RECEIVER_PORT < $1
nc -v localhost $DATA_RECEIVER_PORT < "$DATAFILE"
