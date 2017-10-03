pragma solidity ^0.4.8;
//v0.1.0
import "./Ownable.sol";
import "./SafeMath.sol";
import "./ERC20.sol";
import "./TokenSpender.sol";
contract FaucetRLC is ERC20, SafeMath, Ownable {

    /* Public variables of the token */
  string public name;       //fancy name
  string public symbol;
  uint8 public decimals;    //How many decimals to show.
  string public version = 'v0.1'; 
  uint public initialSupply;
  uint public totalSupply;
  bool public locked;
  //uint public unlockBlock;

  mapping(address => uint) balances;
  mapping (address => mapping (address => uint)) allowed;


  /*
   *  The FaucetRLC Token created with the time at which the crowdsale end
   */

  function FaucetRLC(address faucetAgent1, address faucetAgent2, address faucetAgent3) {

    initialSupply = 87000000000000000;
    totalSupply = initialSupply;
    balances[faucetAgent1] = 29000000000000000;
    balances[faucetAgent2] = 29000000000000000;
    balances[faucetAgent3] = 29000000000000000;
    name = 'iEx.ec Network Token';        // Set the name for display purposes     
    symbol = 'RLC';                       // Set the symbol for display purposes  
    decimals = 9;                        // Amount of decimals for display purposes
  }


  // function for Test net only. not in RLC Token
  function refill(address _to, uint _value) onlyOwner returns (bool) {
    balances[_to] = safeAdd(balances[_to], _value);
    totalSupply = safeAdd(totalSupply, _value);
    Transfer(msg.sender, _to, _value);
    return true;
  }

  // function for Test net only. not in RLC Token
  function forceApprove(address _giver, address _spender, uint _value) onlyOwner returns (bool) {
    allowed[_giver][_spender] = _value;
    Approval(_giver, _spender, _value);
    return true;
  }

  // function for Test net only. not in RLC Token
  function forceBurn(address _toburn,uint256 _value) onlyOwner returns (bool){
    balances[_toburn] = safeSub(balances[_toburn], _value) ;
    totalSupply = safeSub(totalSupply, _value);
    Transfer(_toburn, 0x0, _value);
    return true;
  }


  function burn(uint256 _value) returns (bool){
    balances[msg.sender] = safeSub(balances[msg.sender], _value) ;
    totalSupply = safeSub(totalSupply, _value);
    Transfer(msg.sender, 0x0, _value);
    return true;
  }

  function transfer(address _to, uint _value) returns (bool) {
    balances[msg.sender] = safeSub(balances[msg.sender], _value);
    balances[_to] = safeAdd(balances[_to], _value);
    Transfer(msg.sender, _to, _value);
    return true;
  }

  function transferFrom(address _from, address _to, uint _value) returns (bool) {
    var _allowance = allowed[_from][msg.sender];
    
    balances[_to] = safeAdd(balances[_to], _value);
    balances[_from] = safeSub(balances[_from], _value);
    allowed[_from][msg.sender] = safeSub(_allowance, _value);
    Transfer(_from, _to, _value);
    return true;
  }

  function balanceOf(address _owner) constant returns (uint balance) {
    return balances[_owner];
  }

  function approve(address _spender, uint _value) returns (bool) {
    allowed[msg.sender][_spender] = _value;
    Approval(msg.sender, _spender, _value);
    return true;
  }

    /* Approve and then comunicate the approved contract in a single tx */
  function approveAndCall(address _spender, uint256 _value, bytes _extraData){    
      TokenSpender spender = TokenSpender(_spender);
      if (approve(_spender, _value)) {
          spender.receiveApproval(msg.sender, _value, this, _extraData);
      }
  }

  function allowance(address _owner, address _spender) constant returns (uint remaining) {
    return allowed[_owner][_spender];
  }
  
}

