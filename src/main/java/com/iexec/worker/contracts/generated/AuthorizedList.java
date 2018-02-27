package com.iexec.worker.contracts.generated;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.2.0.
 */
public class AuthorizedList extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b6040516020806107938339810160405280805160008054600160a060020a03191633600160a060020a0316178082559193508392509060a060020a60ff0219167401000000000000000000000000000000000000000083600181111561007157fe5b02179055505061070d806100866000396000f3006060604052600436106100c45763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630d392cd981146100c9578063361895c8146100ef5780633af32abf1461012257806349c0a824146101415780636c2e5bd2146101605780639155e0831461017f578063aff177ca146101a3578063bb0e69e8146101f6578063d821906d14610249578063deff41c114610262578063ed6a596514610291578063f2fde38b146102b0578063f6117546146102cf575b600080fd5b34156100d457600080fd5b6100ed600160a060020a03600435166024351515610306565b005b34156100fa57600080fd5b61010e600160a060020a0360043516610392565b604051901515815260200160405180910390f35b341561012d57600080fd5b61010e600160a060020a03600435166103d5565b341561014c57600080fd5b61010e600160a060020a03600435166103f3565b341561016b57600080fd5b61010e600160a060020a0360043516610408565b341561018a57600080fd5b6100ed600160a060020a0360043516602435151561041d565b34156101ae57600080fd5b6100ed60046024813581810190830135806020818102016040519081016040528093929190818152602001838360200280828437509496505050509135151591506104a99050565b341561020157600080fd5b6100ed60046024813581810190830135806020818102016040519081016040528093929190818152602001838360200280828437509496505050509135151591506104fe9050565b341561025457600080fd5b6100ed60ff6004351661054e565b341561026d57600080fd5b610275610609565b604051600160a060020a03909116815260200160405180910390f35b341561029c57600080fd5b61010e600160a060020a0360043516610618565b34156102bb57600080fd5b6100ed600160a060020a0360043516610636565b34156102da57600080fd5b6102e26106d1565b604051808260018111156102f257fe5b60ff16815260200191505060405180910390f35b60005433600160a060020a0390811691161461032157600080fd5b600160a060020a03821660009081526001602052604090819020805460ff19168315151790557f3f50d04114467f3ca25c50f1540e2864f8a852c0380bbbbb603ebfda10b5baf2908390839051600160a060020a039092168252151560208201526040908101905180910390a15050565b60008060005460a060020a900460ff1660018111156103ad57fe5b14156103c3576103bc826103d5565b90506103d0565b6103cc82610618565b1590505b919050565b600160a060020a031660009081526001602052604090205460ff1690565b60026020526000908152604090205460ff1681565b60016020526000908152604090205460ff1681565b60005433600160a060020a0390811691161461043857600080fd5b600160a060020a03821660009081526002602052604090819020805460ff19168315151790557f739f0e2acc6e65f7a52f8eabe2ca426ca0fb5158e8232d7bcd21bebd59b4ea52908390839051600160a060020a039092168252151560208201526040908101905180910390a15050565b6000805433600160a060020a039081169116146104c557600080fd5b5060005b82518110156104f9576104f18382815181106104e157fe5b9060200190602002015183610306565b6001016104c9565b505050565b6000805433600160a060020a0390811691161461051a57600080fd5b5060005b82518110156104f95761054683828151811061053657fe5b906020019060200201518361041d565b60010161051e565b60005433600160a060020a0390811691161461056957600080fd5b6000547f587143794c2ab5603767c7ce3b183f9a3fca65156acc90d8ce888ffeabece64f9060a060020a900460ff1682604051808360018111156105a957fe5b60ff1681526020018260018111156105bd57fe5b60ff1681526020019250505060405180910390a16000805482919074ff0000000000000000000000000000000000000000191660a060020a83600181111561060157fe5b021790555050565b600054600160a060020a031681565b600160a060020a031660009081526002602052604090205460ff1690565b60005433600160a060020a0390811691161461065157600080fd5b600160a060020a038116151561066657600080fd5b600054600160a060020a0380831691167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a36000805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b60005460a060020a900460ff16815600a165627a7a72305820239e3277103e1d3aee04296052713e7fb997edd8714e0038a31e5b6bc6a6ed6b0029";

    protected AuthorizedList(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected AuthorizedList(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<PolicyChangeEventResponse> getPolicyChangeEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("PolicyChange", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}, new TypeReference<Uint8>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<PolicyChangeEventResponse> responses = new ArrayList<PolicyChangeEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            PolicyChangeEventResponse typedResponse = new PolicyChangeEventResponse();
            typedResponse.oldPolicy = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newPolicy = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PolicyChangeEventResponse> policyChangeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("PolicyChange", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}, new TypeReference<Uint8>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PolicyChangeEventResponse>() {
            @Override
            public PolicyChangeEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                PolicyChangeEventResponse typedResponse = new PolicyChangeEventResponse();
                typedResponse.oldPolicy = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.newPolicy = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<WhitelistChangeEventResponse> getWhitelistChangeEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("WhitelistChange", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<WhitelistChangeEventResponse> responses = new ArrayList<WhitelistChangeEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            WhitelistChangeEventResponse typedResponse = new WhitelistChangeEventResponse();
            typedResponse.actor = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.isWhitelisted = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<WhitelistChangeEventResponse> whitelistChangeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("WhitelistChange", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, WhitelistChangeEventResponse>() {
            @Override
            public WhitelistChangeEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                WhitelistChangeEventResponse typedResponse = new WhitelistChangeEventResponse();
                typedResponse.actor = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.isWhitelisted = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<BlacklistChangeEventResponse> getBlacklistChangeEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("BlacklistChange", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<BlacklistChangeEventResponse> responses = new ArrayList<BlacklistChangeEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            BlacklistChangeEventResponse typedResponse = new BlacklistChangeEventResponse();
            typedResponse.actor = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.isBlacklisted = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BlacklistChangeEventResponse> blacklistChangeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("BlacklistChange", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, BlacklistChangeEventResponse>() {
            @Override
            public BlacklistChangeEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                BlacklistChangeEventResponse typedResponse = new BlacklistChangeEventResponse();
                typedResponse.actor = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.isBlacklisted = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("OwnershipTransferred", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<OwnershipTransferredEventResponse> ownershipTransferredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("OwnershipTransferred", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<TransactionReceipt> updateWhitelist(String _actor, Boolean _isWhitelisted) {
        Function function = new Function(
                "updateWhitelist", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_actor), 
                new org.web3j.abi.datatypes.Bool(_isWhitelisted)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> isActorAllowed(String _actor) {
        Function function = new Function("isActorAllowed", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_actor)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<Boolean> isWhitelisted(String _actor) {
        Function function = new Function("isWhitelisted", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_actor)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<Boolean> m_blacklist(String param0) {
        Function function = new Function("m_blacklist", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<Boolean> m_whitelist(String param0) {
        Function function = new Function("m_whitelist", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> updateBlacklist(String _actor, Boolean _isBlacklisted) {
        Function function = new Function(
                "updateBlacklist", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_actor), 
                new org.web3j.abi.datatypes.Bool(_isBlacklisted)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> updateWhitelist(List<String> _actors, Boolean _isWhitelisted) {
        Function function = new Function(
                "updateWhitelist", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(_actors, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.Bool(_isWhitelisted)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> updateBlacklist(List<String> _actors, Boolean _isBlacklisted) {
        Function function = new Function(
                "updateBlacklist", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(_actors, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.Bool(_isBlacklisted)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> changeListPolicy(BigInteger _policyEnum) {
        Function function = new Function(
                "changeListPolicy", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(_policyEnum)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> m_owner() {
        Function function = new Function("m_owner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Boolean> isblacklisted(String _actor) {
        Function function = new Function("isblacklisted", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_actor)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String _newOwner) {
        Function function = new Function(
                "transferOwnership", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> m_policy() {
        Function function = new Function("m_policy", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<AuthorizedList> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger _initialPolicy) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(_initialPolicy)));
        return deployRemoteCall(AuthorizedList.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<AuthorizedList> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger _initialPolicy) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(_initialPolicy)));
        return deployRemoteCall(AuthorizedList.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static AuthorizedList load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new AuthorizedList(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static AuthorizedList load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new AuthorizedList(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class PolicyChangeEventResponse {
        public BigInteger oldPolicy;

        public BigInteger newPolicy;
    }

    public static class WhitelistChangeEventResponse {
        public String actor;

        public Boolean isWhitelisted;
    }

    public static class BlacklistChangeEventResponse {
        public String actor;

        public Boolean isBlacklisted;
    }

    public static class OwnershipTransferredEventResponse {
        public String previousOwner;

        public String newOwner;
    }
}
