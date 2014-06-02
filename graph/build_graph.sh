#!/bin/bash

export DB_USER=root
export DB_PASS=root
export DB_HOST=127.0.0.1
export DB=wikipedia

cd "$(dirname "$0")"

echo "Creating additional tables..."
mysql -u $DB_USER -h $DB_HOST $DB --password=$DB_PASS < additional_tables.sql
echo "Created additional tables"

echo "Filtering history period articles ..."
./process.py
echo "Filtered articlets"

echo "Building graph ..."
./build_graph.py
echo "Graph built"
