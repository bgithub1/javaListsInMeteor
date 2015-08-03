echo "doing initial setup of some example tables"
sh runSetupTableModelsAndSendReceiveLists.sh  "doTableModelsCreate=true"
echo "wait 3 seconds"
sleep 3
echo "add data to the tables"
sh runSetupTableModelsAndSendReceiveLists.sh  "doRemoveTradeItems=true"  "doSendTradeData=true" "doSendPosData=true" "doSendHowToData=true" "doReadTradeData=true" "doReadPosData=true" "doReadHowToData=true" "doPosSubscription=true"

