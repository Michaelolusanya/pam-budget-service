import os
import sys
import re
import time
import json
import argparse
from subprocess import PIPE, run
import flow_testfile
import flow_testuser
import flow_testuserdetails
"""
WARNING: This script is not allowed to have any print:s except for the one at the end of this file, or in the beging i this requirements is not met.
The reason for this is that this script can and will most likley be used from an API, and the api will get the output, therefore the output must be 
the result of this smoke-test! 

"""

######################################################    
############### Argument Configuration ###############
######################################################

parser = argparse.ArgumentParser()
parser.add_argument("-d","--domain", type=str, help="Add the domain of the API")
parser.add_argument("-j","--jwt-token", type=str, help="Add the path to the directory where the apikeys is located to enable the smoke test")
parser.add_argument("-a","--api-mode", action='store_true', help="This indicates that this smoke test is running from an api, and therefore there no need to trow an exception if the test is not approved")
parser.add_argument("-w","--wait", type=int, help="The amount of time in seconds that the smoke test should wait until it starts")
parser.add_argument("-v","--verbose", action='store_true', help="prints more detailed information")


args = parser.parse_args()

jwt_token = None
domain = None

wait = 0

if args.domain:
    domain = args.domain
if args.wait:
    wait = args.wait
if args.jwt_token:
    jwt_token = args.jwt_token
    

if domain == None or jwt_token == None:
    sys_err_msg = (
        "\n\nThis test cannot continue without any --domain or --apikey-path\n"+
        "example: main_auto_test.py --domain localhost:1237 --jwt-token 'Bearer <token>'\n\n\n"
    )
    if args.api_mode == True:
        print(sys_err_msg)
        sys.exit(0)
    else:
        raise Exception(sys_err_msg)
    

def install_requests_silently():
    run_cli_cmd_silently("python3 -m pip install requests")


def run_cli_cmd_silently(cmd):
    result = run(cmd, stdout=PIPE, stderr=PIPE, universal_newlines=True, shell=True)   
    if result.stdout != '':
        return result.stdout
    elif result.stderr != '':
        return result.stderr
    else:
        return None

######################################################    
########## TESTING test_user GROUP ENDPOINTS #########
######################################################

def execute_testuser_endpoint_group():
    test_data = flow_testuser.test_flow(domain, jwt_token)
    return structure_test_data(test_data, "testuser_test_result")

######################################################    
########## TESTING test_user_details GROUP ENDPOINTS #########
######################################################

def execute_testuserdetails_endpoint_group():
    test_data = flow_testuserdetails.test_flow(domain, jwt_token)
    return structure_test_data(test_data, "testuserdetails_test_result")    

######################################################    
########## TESTING test_file GROUP ENDPOINTS #########
######################################################

def execute_testfile_endpoint_group():
    test_data = flow_testfile.test_flow(domain, jwt_token)
    return structure_test_data(test_data, "testfile_test_result")
    

######################################################    
######## TESTING boilerplates GROUP ENDPOINTS ########
######################################################

def execute_boilerplates_endpoint_group():
    #test_data = flow_boilerplates.test_flow(domain, jwt_token, repo_url)
    #return structure_test_data(test_data, "boilerplates_test_result")
    test_data = {"test_approved": False, "test_data": "nothing"}
    return structure_test_data(test_data, "boilerplates_test_result")
     


######################################################    
################ MAIN auto test TOOLS ################
######################################################


def structure_test_data(test_data, test_result_name):
    testfile_test_result = {
        "test_approved": True,
        "test_data": test_data
    }
    if len(test_data) == 0:
        testfile_test_result["test_approved"] = False
        return testfile_test_result

    for test_result in test_data:
        if args.verbose == False and test_result["test_approved"] == True:
            del test_result["data"]

        if test_result["test_approved"] == False:
            testfile_test_result["test_approved"] = False
    return {test_result_name: testfile_test_result}


def is_total_test_approved(total_test_result):
    for sub_test in total_test_result:
        if not sub_test:
            pass #TODO: REMOVE THIS EVENTUALLY
        else:
            for key in sub_test:
                if sub_test[key]["test_approved"] == False:
                    return False   
    return True


def get_result_overview(total_test_result):
    test_overview = []
    for sub_test in total_test_result:
        if sub_test: 
            for test in sub_test:
                if test != "test_data": 
                    test_result = "Approved" if sub_test[test]["test_approved"] == True else "Not Approved"
                    test_overview.append({
                        test: test_result
                    })
    return test_overview


def get_in_json_str(total_test_result):
    result = [total_test_result, get_result_overview(total_test_result)]
    result = json.dumps(result) 
    result = result.replace("'","\"")
    result = result.replace("\n","")
    return result


def start_main():
    #install_requests_silently()

    #time.sleep(wait)
    

    total_test_result = []
    
    
    total_test_result.append(execute_testuser_endpoint_group())
    total_test_result.append(execute_testuserdetails_endpoint_group())    
    total_test_result.append(execute_testfile_endpoint_group())
    

    #TODO: not implemented 
    #total_test_result.append(execute_boilerplates_endpoint_group())
    if args.api_mode == True:
        print(get_in_json_str(total_test_result))
    elif is_total_test_approved(total_test_result) == False:
        raise Exception(get_in_json_str(total_test_result))
    else:
        print(get_in_json_str(total_test_result))
        

start_main()
