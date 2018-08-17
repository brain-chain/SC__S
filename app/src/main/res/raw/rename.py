import os
import string

vowellist = ['a','i','e','o', 'u', 'n' ]
letterlist = [ 'a', 'b', 'v', 'g', 'd', 'e', 'z', 'h', 't', 'y', 'k',
                'l', 'm', 'n', 's', 'p', 'f', 'c', 'r', 'x' ]

filelist =  os.listdir(os.getcwd());
print(filelist)
filelist.remove('rename.py')
i = 0
for f in filelist:
    s = str(i)
    os.rename(f, 'img_'+s+'.mp3')
    i+=1
##j = 0
##for num,v in enumerate(vowellist):
##    #remove silent letters
##    if v == 'n':
##        letterlist.remove('e');
##    for num2,l in enumerate(letterlist):
##        os.rename(filelist[j], v+l+".mp3")
##        j += 1
##    
