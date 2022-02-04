import os
import sys
import random
import copy
import test_tools as tt

####################################################################
####################### EXECUTING FLOW TEST ########################
####################################################################

def test_flow(domain, jwt_token):
    current_path = os.path.dirname(os.path.abspath(__file__))
    test_files = "test_files"
    test_files_dir = os.path.join(current_path,test_files)
    #TODO: changing concatenation for path join
    #test_files_dir = os.path.dirname(os.path.abspath(__file__))+"/test_files/"
     
    file_paths = list_files_in_dir(test_files_dir)
    file_path = file_paths.pop()

    test_result = []

    #############################################################################
    ############### First part of the smoke test: security check ################
    #############################################################################


    url = "{}/api/web/v1/exampleFiles".format(domain)
    test_result.append( tt.execute_file_upload_endpoint_test("", 401, url, file_path) )

    url = "{}/web/v1/exampleFile".format(domain)
    test_result.append( tt.execute_file_upload_endpoint_test("", 401, url, file_path) )

    url = "{}/web/v1/exampleFile/{}".format(domain,"Foo.txt")
    test_result.append( tt.execute_endpoint_test("", 401, url, "GET") )

    url = "{}/web/v1/exampleFile/{}".format(domain,"Foo.txt")
    test_result.append( tt.execute_endpoint_test("", 401, url, "DELETE") )
    
    #TODO: Dino has to fix something up
    #url = "{}/web/v1/testFile".format(domain)
    #test_result.append( tt.execute_endpoint_test("", 404, url, "GET") )
    
    #TODO: No needed
    #url = "{}/web/v1/exampleFile/".format(domain)
    #test_result.append( tt.execute_endpoint_test("", 401, url, "GET") )

    #url = "{}/web/v1/exampleFile".format(domain)
    #test_result.append( tt.execute_endpoint_test("", 401, url, "GET") )

    

    #############################################################################
    ############### Second part of the smoke test: upload files #################
    #############################################################################
    # Multiple
    filenames = []
    url = "{}/api/web/v1/exampleFiles".format(domain) 
    data = tt.execute_file_upload_endpoint_test(jwt_token, 201, url, file_paths)
    filenames = get_filenames_from_resp_data(data, filenames)
    test_result.append(data)
    # Single
    url = "{}/api/web/v1/exampleFile".format(domain) 
    data = tt.execute_file_upload_endpoint_test(jwt_token, 201, url, file_path)
    filenames = get_filenames_from_resp_data(data, filenames)
    test_result.append( data )
    #Double post 
    data = tt.execute_file_upload_endpoint_test(jwt_token, 400, url, file_path)
    filenames = get_filenames_from_resp_data(data, filenames)
    test_result.append( data )

    if len(filenames) < 3:
        return test_result

    
    #############################################################################
    ### Third part of the smoke test: read, delete and download upload files ####
    #############################################################################
    # Get one
    url = "{}/api/web/v1/exampleFile/{}".format(domain, filenames[0])
    test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "GET") )
    # Get multiple
    url_query = "{}/api/web/v1/exampleFiles?{}".format(domain, setup_endpoint_query(filenames) )
    test_result.append( tt.execute_endpoint_test(jwt_token, 200, url_query, "GET") )
    # TODO: Is this needed?  
    #url = "{}/web/v1/testFiles/".format(domain)
    #test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "GET") )
    #url = "{}/web/v1/testFile/download/{}".format(domain, filenames[0])    
    #test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "GET") )

    for fileName in filenames:
        url = "{}/api/web/v1/exampleFile/{}".format(domain, fileName) 
        test_result.append( tt.execute_endpoint_test(jwt_token, 200, url, "DELETE") ) 

    for fileName in filenames:
        url = "{}/api/web/v1/exampleFile/{}".format(domain, fileName)  
        test_result.append( tt.execute_endpoint_test(jwt_token, 404, url, "GET") ) 

    return test_result


####################################################################
#################### TEST-FILE CONFIGURATOIN #######################
####################################################################


def list_files_in_dir(dir_path):
    filenames = []
    for entry in os.scandir(dir_path):
        if entry.is_file():
            filenames.append(entry.path )
            #filenames.append(dir_path+entry.name)
    return filenames


def get_filenames_from_resp_data(response, filename_list = None):
    if filename_list == None:
        filename_list = []
    if is_single_file_with_fields(response):
        filename_list.append(response["data"]["data"]["fileName"])
    elif is_multiple_files_with_fields(response):
        filelist = response["data"]["data"]["success"]
        for file_dict in filelist:
            if "fileName" in file_dict:
                filename_list.append(file_dict["fileName"])
    return filename_list


def is_multiple_files_with_fields(response):  
    return response_has_data_fields(response) and "success" in response["data"]["data"]

def is_single_file_with_fields(response):
    return response_has_data_fields(response) and "fileName" in response["data"]["data"]

def response_has_data_fields(response):
    return "data" in response and "data" in response["data"]
  
def setup_endpoint_query(arr):
    #syntax="filenames=<FILENAME>&"
    #query= "?"
    #for index in arr:
    #    idx = syntax.replace("<FILENAME>",index)
    #    query = query+idx
    #query =  query[:-1]
    my_query = ""
    for filename in arr:
        my_query = my_query + "&fileName="+filename
    my_query = my_query[1:]
    return my_query
     