import os
import sys
import random
import test_tools as tt

####################################################################
####################### EXECUTING FLOW TEST ########################
####################################################################

def test_flow(domain, jwt_token):
    created_id = []
    test_result = []


    #############################################################################
    ############### First part of the smoke test: security check ################
    #############################################################################

    url = "{}/api/web/v1/exampleUser/".format(domain)
    test_result.append( tt.execute_endpoint_test("", 401, url, "GET") )

    url = "{}/api/web/v1/exampleUser/{}".format(domain, "aksfjh879423th")
    test_result.append( tt.execute_endpoint_test("", 401, url, "GET") )

    url = "{}/api/web/v1/exampleUsers/{}".format(domain, setup_endpoint_query(["sdgshgagdazxv","dsagdsnjshsf"]) )
    test_result.append( tt.execute_endpoint_test("", 401, url, "GET") )
    
    url = "{}/api/web/v1/exampleUser".format(domain) 
    test_result.append( tt.execute_endpoint_test("", 401, url, "PUT", get_testuser_example("ffqqdgsdhahgdsdah")) )

    url = "{}/api/web/v1/exampleUsers".format(domain)
    test_result.append( tt.execute_endpoint_test("", 401, url, "PUT", get_testusers_example("fdgsdfrqhahgdsdah")) )

    url = "{}/api/web/v1/exampleUser/{}".format(domain, "fdgsdhahgdsdah")
    test_result.append( tt.execute_endpoint_test("", 401, url, "DELETE") )

    url = "{}/api/web/v1/exampleUser/{}".format(domain, "gfsdgsdhshah")
    test_result.append( tt.execute_endpoint_test("", 401, url, "GET") )

    url = "{}/api/web/v1/exampleUsers/{}".format(domain, setup_endpoint_query(["sdgshgagdazxv","dsagdsnjshsf"]) )
    test_result.append( tt.execute_endpoint_test("", 401, url, "GET") )


    #############################################################################
    ############# Second part of the smoke test: create exampleUsers ###############
    #############################################################################

    url = "{}/api/web/v1/exampleUser".format(domain)
    data = tt.execute_endpoint_test(jwt_token, 201, url, "POST", get_testuser_example())
    created_id = get_created_id_from_data(data, created_id)
    test_result.append( data )

    url = "{}/api/web/v1/exampleUsers".format(domain)
    data = tt.execute_endpoint_test(jwt_token, 201, url, "POST", get_testusers_example())
    created_id = get_created_id_from_data(data, created_id)
    test_result.append( data )

    if len(created_id) < 3:
        return test_result

  
    #############################################################################
    ##### Third part of the smoke test: read, delete and update exampleUsers #######
    #############################################################################

    url = "{}/api/web/v1/exampleUsers/".format(domain)
    test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "GET") )

    url = "{}/api/web/v1/exampleUser/{}".format(domain, created_id[0])
    test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "GET") )

    url = "{}/api/web/v1/exampleUsers/{}".format(domain, setup_endpoint_query(created_id) )
    test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "GET") )
    
    url = "{}/api/web/v1/exampleUser".format(domain) 
    test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "PUT", get_testuser_example(created_id[0])) )

    url = "{}/api/web/v1/exampleUsers".format(domain)
    test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "PUT", get_testusers_example(created_id)) )

    for id in created_id:
        url = "{}/api/web/v1/exampleUser/{}".format(domain, id)
        test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "DELETE") )

    url = "{}/api/web/v1/exampleUser/{}".format(domain, created_id[0])
    test_result.append( tt.execute_endpoint_test(jwt_token, 404, url, "GET") )

    url = "{}/api/web/v1/exampleUsers/{}".format(domain, setup_endpoint_query(created_id) )
    test_result.append( tt.execute_endpoint_test(jwt_token, 404, url, "GET") )

    return test_result


####################################################################
#################### TEST-USER  CONFIGURATOIN ######################
####################################################################

def get_testusers_example(id_list = None):
    test_users = []
    if isinstance(id_list, list):
        for id in id_list:
            test_users.append(get_testuser_example(id))
    else:
        test_users.append(get_testuser_example())
        test_users.append(get_testuser_example())
    return test_users


def get_created_id_from_data(data, created_id = None):
    if isinstance(data, dict) and isinstance(data.get("data") , dict):
        data = data.get("data")   
    if created_id == None:
        created_id = []
    if isinstance(data, dict) and isinstance(data.get("data") , dict):
        test_user = data.get("data")
        if test_user.get("id") != None:
            created_id.append(test_user.get("id"))
    elif isinstance(data, dict) and isinstance(data.get("data") , list):
        test_user_list = data.get("data")
        for test_user in test_user_list:
            if test_user.get("id") != None:
                created_id.append(test_user.get("id"))
        
    return created_id
 

 

def get_random_lastname():
    lastname_list = ["Ericsson","Smith","Rodriguez","Hussein","Lee","Baker","Svensson","Patel","Schweinstaiger","Sanders"]
    return get_random_list_index(lastname_list)


def get_random_fname():
    female_fname = ["Donna","Barbara","Sarah","Fatima","Elin","Ardiana","Johanna","Penolope","Lydia","Elba","Popov","Mark","Joseph","Adam","Mohammed","Vasilji","Roman","Juan","Frank","Ivar","Sven"]
    return get_random_list_index(female_fname)
 
def get_random_address():
    address_list = ["Mercury road 77", "Europe road 24", "West addington road 34", "Validmir Reshtov road 45", "Jon Smiths road 71", "Asia road 36"]
    return get_random_list_index(address_list)


def get_testuser_example(testuser_id = None):
    test_user = {} 
    test_user["firstName"] = get_random_fname()  
    test_user["lastName"] = get_random_lastname()
    test_user["email"] = "{}.{}@{}.com".format(test_user["firstName"], test_user["lastName"], tt.random_str(7))  
    return test_user

def get_user_details_example():
    test_details = {}
    test_details["adress"] = get_random_address()
    test_details["phonenumber"] = "123121212"
    test_details["dateOfBirth"] = "1990-01-01"
    return test_details

def get_random_list_index(spec_list):
    index = random.randint(0, (len(spec_list )-1) )
    return spec_list[index]


def setup_endpoint_query(arr):
    syntax="id_list=<ID_LIST>&"
    query= "?"
    for index in arr:
        idx = syntax.replace("<ID_LIST>",index)
        query = query+idx
    query =  query[:-1]
    return query