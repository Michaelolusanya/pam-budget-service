import os
import sys
import random
import test_tools as tt
import flow_testuser

####################################################################
####################### EXECUTING FLOW TEST ########################
####################################################################


def test_flow(domain, jwt_token):
    created_id = []
    test_result = []


    #############################################################################
    ############### First part of the smoke test: security check ################
    #############################################################################

    url = "{}/api/web/v1/exampleUserDetails/".format(domain)
    test_result.append( tt.execute_endpoint_test("", 401, url, "GET") )

    url = "{}/api/web/v1/exampleUserDetails/{}".format(domain, "1231231312")
    test_result.append( tt.execute_endpoint_test("", 401, url, "GET") )

    url = "{}/api/web/v1/exampleUserDetails/{}".format(domain, "1231231312")
    test_result.append( tt.execute_endpoint_test("", 401, url, "GET") )
    
    url = "{}/api/web/v1/exampleUserDetails".format(domain) 
    test_result.append( tt.execute_endpoint_test("", 401, url, "PUT", get_user_details_example(0)) ) 

    url = "{}/api/web/v1/exampleUserDetails/{}".format(domain, "1231231312")
    test_result.append( tt.execute_endpoint_test("", 401, url, "DELETE") )

    url = "{}/api/web/v1/exampleUserDetails/{}".format(domain, "1231231312")
    test_result.append( tt.execute_endpoint_test("", 401, url, "PATCH", get_user_details_example(0)) ) 

    url = "{}/api/web/v1/exampleUserDetails".format(domain) 
    test_result.append( tt.execute_endpoint_test("", 401, url, "POST", get_user_details_example(0)) ) 

    

    #PLURAL

    url = "{}/api/web/v1/exampleUsersDetails/{}".format(domain, setup_endpoint_query(["1231231312","9879878979879"]) )
    test_result.append( tt.execute_endpoint_test("", 401, url, "GET") )

    url = "{}/api/web/v1/exampleUsersDetails/{}".format(domain, [get_user_details_example(0)])
    test_result.append( tt.execute_endpoint_test("", 401, url, "POST") )

    ###################################################################
    ############# PREPARATIONS BEFORE TEST USER DETAILS ###############
    ###################################################################
    url = "{}/api/web/v1/exampleUser".format(domain)
    user = flow_testuser.get_testuser_example()
    data = tt.execute_endpoint_test(jwt_token, 201, url, "POST", user)
    flow_test_get_id = flow_testuser.get_created_id_from_data(data, [])
    USER_ID = flow_test_get_id[0]
    
    ########################################################################################
    ############# Second part of the smoke test: create exampleUsersDetails ###############
    ########################################################################################

    url = "{}/api/web/v1/exampleUsersDetails".format(domain)
    body = [get_user_details_example(USER_ID),get_user_details_example(USER_ID)]
    data = tt.execute_endpoint_test(jwt_token, 201, url, "POST",body)
    created_id = get_created_id_from_data(data, created_id)
    test_result.append( data )

    url = "{}/api/web/v1/exampleUserDetails".format(domain)
    data = tt.execute_endpoint_test(jwt_token, 201, url, "POST", get_user_details_example(USER_ID))
    created_id = get_created_id_from_data(data, created_id)
    test_result.append( data )

    if len(created_id) < 3:
        return test_result

  
    ########################################################################################
    ##### Third part of the smoke test: read, delete and update exampleUsersDetails #######
    ########################################################################################

    url = "{}/api/web/v1/exampleUsersDetails/".format(domain)
    test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "GET") )

    url = "{}/api/web/v1/exampleUserDetails/{}".format(domain, created_id[0])
    test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "GET") )

    url = "{}/api/web/v1/exampleUsersDetails/{}".format(domain, setup_endpoint_query(created_id) )
    test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "GET") )
    
    url = "{}/api/web/v1/exampleUserDetails/{}".format(domain,created_id[0])
    test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "PATCH", get_user_details_example(created_id[0])) )

    url = "{}/api/web/v1/exampleUserDetails/{}".format(domain,created_id[0])
    test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "PUT", get_user_details_example(created_id[0])) )

    for id in created_id:
        url = "{}/api/web/v1/exampleUserDetails/{}".format(domain, id)
        test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "DELETE") )

    url = "{}/api/web/v1/exampleUserDetails/{}".format(domain, created_id[0])
    test_result.append( tt.execute_endpoint_test(jwt_token, 404, url, "GET") )

    url = "{}/api/web/v1/exampleUsersDetails/{}".format(domain, setup_endpoint_query(created_id) )
    test_result.append( tt.execute_endpoint_test(jwt_token, 404, url, "GET") )

    return test_result


####################################################################
#################### TEST-USER  CONFIGURATOIN ######################
####################################################################
 

def get_testusersdetails_example(id_list = None):
    test_users = []
    if isinstance(id_list, list):
        for id in id_list:
            test_users.append(get_random_address())
    else:
        test_users.append(get_random_address())
        test_users.append(get_random_address())
    return test_users


def get_created_id_from_data(response_body, created_id = None):
    if not isinstance(response_body, dict):
        return
    if created_id == None:
        created_id = []

    if isinstance(response_body.get("data") , dict):
        data = response_body.get("data").get("data") 

    if "success" in data and isinstance(data.get("success"), list):  
        for test_user in data.get("success"):        
            if test_user.get("id") != None:
                created_id.append(test_user.get("id"))
           
    elif isinstance(data , dict) and data.get("id"): 
        created_id.append(data.get("id"))

    return created_id
 

 
 
def get_random_address():
    address_list = ["Mercury road 77", "Europe road 24", "West addington road 34", "Validmir Reshtov road 45", "Jon Smiths road 71", "Asia road 36"]
    return get_random_list_index(address_list)

 

def get_user_details_example(id):
    test_details = {}
    test_details["adress"] = get_random_address()
    test_details["phonenumber"] = str(get_random_phone())
    test_details["dateOfBirth"] = "1990-01-01"
    test_details["exampleUserId"] = id
    return test_details



def get_random_phone():
    return random.randint(100000, 999999)

def get_random_list_index(spec_list):
    index = random.randint(0, (len(spec_list )-1) )
    return spec_list[index]


def setup_endpoint_query(arr):
    syntax="id_list=<ID_LIST>&"
    query= "?"
    for index in arr:
        idx = syntax.replace("<ID_LIST>", str(index))
        query = query+idx
    query =  query[:-1]
    return query