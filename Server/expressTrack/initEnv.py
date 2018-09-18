#!/usr/bin/python
# coding=utf-8

import sys
import re
import os

if __name__ == "__main__":
    print("1. Install OrderTrack's path to python:")
    for item in sys.path:
        if item.find("dist-packages") > 0:
            curDir = sys.path[0]
            if os.path.isfile(curDir):
                curDir = os.path.dirname(curDir)            
            targetPath = re.sub('dist-packages/.*', "dist-packages/", item) + "/OrderTrack.pth"
            
            f = open(targetPath, mode='w')
            f.write(curDir)
            f.close()
            print("    success: currentPath(%s) has been add to PATH env (%s)!"%(curDir, targetPath))
            break
    else:
        print("    failed: can not find the python path!")
        exit()
        
    print("OrderTrack init finished！\n")
    
