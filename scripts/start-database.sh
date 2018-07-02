#!/usr/bin/env bash
set -e

here=$(dirname $0)

# If the database is already running, stop it
if docker top db &>/dev/null; then
    echo "Stopping database"
    $here/../auth-db/scripts/stop.sh
    sleep 1s
fi

echo "Starting database"
$here/../auth-db/scripts/start.sh $DB_VERSION $DB_PORT

echo "-------------------------------------------------------------------------"
echo "Database is now accessible at port $DB_PORT"

