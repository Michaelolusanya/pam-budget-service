import time
import uuid
import datetime
import re
import os
import random
import json
import random
import requests
import copy
from pathlib import Path

def execute_endpoint_test(jwt_token, expected_status_code, url, method, payload = None):
    '''
    url: It replaces quotation marks with their escaped version because long url's can include quotation marks.
    '''
    test_approved = None
    resp = http_request(jwt_token,method,url,payload)
    test_approved = (expected_status_code == resp["status_code"])
    url_result = url.replace("'",'\\"')
    return {
        "url": url_result,
        "method": method,
        "expected_status_code": expected_status_code,
        "status_code": resp["status_code"],
        "test_approved": test_approved,
        "data": resp["data"]
    }

def execute_file_upload_endpoint_test(jwt_token, expected_status_code, url, filepaths):
    test_approved = None
    resp = http_file_upload(jwt_token,url,filepaths)
    test_approved = True if expected_status_code == resp["status_code"] else False
    return {
        "url": url,
        "method": "POST",
        "expected_status_code": expected_status_code,
        "status_code": resp["status_code"],
        "test_approved": test_approved,
        "data": resp["data"]
    }


def http_request(jwt_token, method , url, payload = None ):
    headers = {
            "Authorization": jwt_token,
            "Content-type": "application/json"
        }
    resp = {}
    if payload != None:
        payload = json.dumps(payload).encode('utf8') 

    try:
        response = None

        if method == "GET":
            response = requests.get(url, headers=headers)
        elif method == "POST":
            response = requests.post(url, headers=headers, data=payload)
        elif method == "PUT":
            response = requests.put(url, headers=headers, data=payload)
        elif method == "DELETE":
            response = requests.delete(url, headers=headers)
        elif method == "PATCH":
            response = requests.patch(url, headers=headers, data=payload)
        resp["status_code"] = int( response.status_code )

        if resp["status_code"] == 200 and response.headers["Content-Type"] != "application/json":
            resp["data"] = {"file_type": response.headers["Content-Type"], "downloaded": True}
        elif resp["status_code"] == 200:
            resp["data"] = minimize_data(json.loads( response.text ) ) 
        else:
            resp["data"] = json.loads(response.text) 
    except Exception as e:
        exception_str = copy.deepcopy(e)
        err_msg = get_json_secure_string(str(exception_str))
        resp["status_code"] = 500
        resp["data"] = {"success": False, "err_msg": err_msg}
    time.sleep(1)
    return resp
    

def http_file_upload(jwt_token, url, filepaths):
    headers = {
            "Authorization": jwt_token
        }
    resp = {}
    response = {}
    try:
        if  isinstance(filepaths, list) and len(filepaths) > 1:
            resp = __upload_multiple_files(url, headers, filepaths)
        elif isinstance(filepaths, str):
            resp = __upload_single_file(url, headers, filepaths)
           
    except Exception as e:
        exception_str = copy.deepcopy(e)
        err_msg = get_json_secure_string(str(exception_str))
        resp["status_code"] = 500
        resp["data"] = {"success": False, "err_msg": err_msg}
    time.sleep(1)
    return resp


def __upload_multiple_files(url, headers, filepaths):
    resp = {}
    files = []
    for filepath in filepaths:
        files.append(("files", (get_filename_from_path(filepath) ,read_file(filepath) ) ) ) 
    response = requests.post(url, files=files, headers=headers)        
    resp["status_code"] = int( response.status_code )
    if resp["status_code"] >= 200 and resp["status_code"] <203:
        resp["data"] = minimize_data(json.loads( response.text ) ) 
    else:
        resp["data"] = json.loads(response.text)
    return resp 


def __upload_single_file(url, headers, filepath):
    resp = {}
    with open(filepath, "rb") as a_file: 
        response = requests.post(url, files={"file": (get_filename_from_path(filepath), a_file)}, headers=headers)
        resp["status_code"] = int( response.status_code )
        if resp["status_code"] >= 200 and resp["status_code"] <203:
           resp["data"] = minimize_data(json.loads( response.text ) ) 
        else:
            resp["data"] = json.loads(response.text) 
    return resp
    
    #url = "http://localhost:8080/api/web/v1/exampleFile"
    #file_name = get_filename_from_path(filepath)
    #payload={}
    #files=[ ('file',(file_name,open(filepath,'rb'),'application/octet-stream')) ]
    #headers = { 'accept': '*/*' }

    #response = requests.request("POST", url, headers=headers, data=payload, files=files)
    #resp["status_code"] = int( response.status_code )




def minimize_data(data):
    if "data" in data.keys() and type(data["data"]) is list and len(data["data"]) > 3:
        temp_data = [] 
        size = len(data["data"])
        temp_data.append(data["data"][size-1])
        temp_data.append(data["data"][size-2])
        temp_data.append(data["data"][size-3])
        data["data"] = temp_data
        
    return data

def get_put_param_list_from_id_list(id_list):
    put_param_id = ""
    for _id_ in id_list:
        put_param_id = put_param_id + _id_ + ","
    return put_param_id[:-1]


def rand_str(length):
    characters = list("zxcvbnmasdfghjklpoiuytrewq1234560987QMWNEBRVTCYXUZIOPASLDKFJGH")
    random_string = ""
    for i in range(length):
        index = random.randint(0, len(characters )-1) 
        random_string = random_string + characters[index]
    return random_string
    

def shuffle_str(_string_):
    _string_ = list(_string_)
    random.shuffle(_string_)
    return "".join(_string_)


def generate_uniq_id():
    return random_str(1) + str(re.sub('\.', '', str(time.time())))[1:12] + str(re.sub('\-', '', str(uuid.uuid4())))[0:4]


def get_current_datetime(additional_seconds = None):
    timestamp = get_current_timestamp(additional_seconds)
    return timestamp_to_str_datetime(timestamp)
    
    
def get_current_timestamp(additional_seconds = None):
    add_seconds = 0
    if additional_seconds != None:
        add_seconds = additional_seconds
    return int(time.time()+add_seconds)
    
    
def timestamp_to_str_datetime(timestamp):
    return str(datetime.datetime.fromtimestamp(timestamp).strftime('%Y-%m-%d %H:%M:%S'))

    
def str_datetime_2_timestamp(str_datetime):
    if "/" in str_datetime:
        date_time = datetime.datetime.strptime(str_datetime, '%Y/%m/%d %H:%M:%S')
        return int(datetime.datetime.timestamp(date_time))
    else:
        date_time = datetime.datetime.strptime(str_datetime, '%Y-%m-%d %H:%M:%S')
        return int(datetime.datetime.timestamp(date_time))


def random_str(length):
    characters = shuffle_str("zxcvbnmasdfghjklpoiuytrewq1234560987QMWNEBRVTCYXUZIOPASLDKFJGH")
    random_string = ""
    for i in range(length):
        index = random.randint(0, len(characters ) )
        index = index - 1
        random_string = random_string + characters[index]
    return random_string


def get_json_secure_string(string):
    string = string.replace("'", "")
    string = string.replace('"', '')
    return string


def get_filename_from_path(path):
    try:
        filename = Path(path).name
        return str(filename)
    except:
        return None


def read_file(filepath):
    data = None
    with open(filepath, "rb") as file:
        data = file.read(1024)
    
    return data

