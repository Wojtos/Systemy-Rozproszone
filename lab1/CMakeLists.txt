cmake_minimum_required(VERSION 3.12)
project(lab1)

set(CMAKE_CXX_STANDARD 14)

add_executable(lab1 UDPConnection.cpp UDPConnection.h)
target_link_libraries(lab1 pthread)
