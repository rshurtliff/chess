package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;

public class AdminService {
    private DataAccess dataAccess;

    public AdminService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public void clear(){
        dataAccess.clear();
    }
}
