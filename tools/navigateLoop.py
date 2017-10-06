import qi
session = qi.Session()
session.connect("tcp://10.0.204.143:9559")
nav = session.service("ALNavigation")

while True:
    path = nav.getExplorationPath()
    try:
        print " NavigateTo first Node"
        first = path[1]
        boo = nav.navigateToInMap(first[0], first[1])

        print " NavigateTo last Node"
        last = path[19]
        boo4 = nav.navigateToInMap(last[0], last[1])

    except Exception, e:
        print "Error :"
        print str(e)
