-- Keep a log of any SQL queries you execute as you solve the mystery.

-- Find the crime scene report for the theft on July 28, 2023, on Humphrey Street
SELECT * FROM crime_scene_reports
WHERE date = '2023-07-28' AND location = 'Humphrey Street';

-- Get the witness statements for the crime scene report
SELECT * FROM witness_statements
WHERE crime_scene_report_id = (SELECT id FROM crime_scene_reports
WHERE date = '2023-07-28' AND location = 'Humphrey Street');

-- Find the person with the distinctive characteristic (e.g., tattoo)
SELECT * FROM people
WHERE tattoo = ' distinctive tattoo description ';

-- Find the flight information for the thief
SELECT * FROM flights
WHERE passenger_id = (SELECT id FROM people
WHERE tattoo = ' distinctive tattoo description ');

-- Find the person who helped the thief escape
SELECT * FROM people
WHERE id IN (SELECT helper_id FROM flights
WHERE passenger_id = (SELECT id FROM people
WHERE tattoo = ' distinctive tattoo description '));
