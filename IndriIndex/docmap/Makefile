# XXX Configure your path to indri
INDRISRC=$(HOME)/indri
include $(INDRISRC)/MakeDefns
INCPATH=-I$(INDRISRC)/include $(patsubst %, -I$(INDRISRC)/contrib/%/include, $(DEPENDENCIES)) 
LIBPATH=-L$(INDRISRC)/obj  $(patsubst %, -L$(INDRISRC)/contrib/%/obj, $(DEPENDENCIES))
LIBS=-lindri $(patsubst %, -l%, $(DEPENDENCIES))
SHARED=

all:
	$(CXX) $(CXXFLAGS) -g -std=c++11 docmap.cpp -o docmap $(LIBPATH) $(LIBS) $(CPPLDFLAGS)


