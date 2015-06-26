echo ""
echo " below are examples of java command line arguments:"
echo '"meteorUrl=localhost" "meteorPort=3000" "adminEmail="admin1@demo.com" "adminPass=admin1" "doRemoveTradeItems=true"  "doTableModelsCreate=true" "doSendTradeData=true" "doSendPosData=true" "doSendHowToData=true" "doReadTradeData=true" "doReadPosData=true" "doReadHowToData=true" "doPosSubscription=true"'
echo " this example only uses the first 4"
sh mvnexec.sh com.billybyte.meteorjava.runs.SetupTableModelsAndSendReceiveLists  "metUrl=localhost" "metPort=3000" "adminEmail=admin1@demo.com" "adminPass=admin1" ${*:1}
