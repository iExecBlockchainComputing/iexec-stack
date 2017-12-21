-- ===========================================================================
-- 
--  Copyright 2014  E. URBAH
--                  at LAL, Univ Paris-Sud, IN2P3/CNRS, Orsay, France
--  License GPL v3
-- 
--  XtremWeb-HEP Tables for Offering, Provisioning and Billing :
--  SQL script inserting example rows in tables 'Customers', 'CustomerDetails'
--  and 'CustomerContracts' for 'Oleg LODYGENSKY fisrt name'.
-- 
-- ===========================================================================
insert  into  Customers
             (CustomerId, CustomerAccount, CustomerBalance, CustomerDescription)
      values (         1,          'oleg',               0, 'Oleg LODYGENSKY fisrt name');

insert  into  CustomerDetails
             (CustomerId, CustomerDetailsStartDate, CustomerName,                 CustomerBillingAddress)
      values (         1,             '2000-01-01', 'Oleg LODYGENSKY fisrt name', 'LAL,  Bat 200,  91898 ORSAY,  France');

insert  into  CustomerContracts
             (CustomerId, TariffPlanId, CustomerContractStartDate, CustomerContractEndDate, CustomerContractDescription)
      values (         1,            6,              '2000-01-01',            '2012-12-31', 'Extensive - Oleg LODYGENSKY fisrt name'),
             (         1,            2,              '2013-01-01',            '2013-12-31', 'Trivial   - Oleg LODYGENSKY fisrt name'),
             (         1,            5,              '2014-01-01',            '2014-12-31', 'Regular   - Oleg LODYGENSKY fisrt name');
