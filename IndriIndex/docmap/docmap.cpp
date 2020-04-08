#include <indri/Repository.hpp>
#include <indri/CompressedCollection.hpp>
#include <indri/LocalQueryServer.hpp>
#include <iostream>

int main (int argc, char** argv) {

  if (argc != 2) {
    std::cout << "USAGE: " << argv[0];
    std::cout << " <indri repository>" << std::endl;
    return EXIT_FAILURE;
  }
  std::string repository_name = argv[1];

  indri::collection::Repository repo;
  repo.openRead(repository_name); 
  indri::server::LocalQueryServer local(repo);
  size_t doc_count = local.documentCount();
  indri::collection::CompressedCollection *collection = repo.collection();
  
  for (size_t i = 1; i <= doc_count; ++i) {
    std::cout << collection->retrieveMetadatum( i , "docno" ) << std::endl;
  }

  return EXIT_SUCCESS;
}


