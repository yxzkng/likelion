
-- 술집 데이터
INSERT INTO store (name, address, open_time, close_time, seat_capacity) VALUES ('낭만오지', '정문 도보 1분', '17:00:00', '00:00:00', 12);
INSERT INTO store (name, address, open_time, close_time, seat_capacity) VALUES ('캠퍼스 맥주창고', '후문 도보 3분', '17:00:00', '00:00:00', 8);
INSERT INTO store (name, address, open_time, close_time, seat_capacity) VALUES ('술이야기', '정문 도보 5분', '18:00:00', '00:00:00', 20);
INSERT INTO store (name, address, open_time, close_time, seat_capacity) VALUES ('달빛포차', '후문 도보 1분', '19:00:00', '00:00:00', 6);
INSERT INTO store (name, address, open_time, close_time, seat_capacity) VALUES ('한잔의 여유', '정문 도보 10분', '16:00:00', '00:00:00', 15);

-- 휴무일
INSERT INTO store_closed_days (store_id, day_of_week) VALUES (2, 'MONDAY');
INSERT INTO store_closed_days (store_id, day_of_week) VALUES (3, 'SUNDAY');
INSERT INTO store_closed_days (store_id, day_of_week) VALUES (4, 'MONDAY');
INSERT INTO store_closed_days (store_id, day_of_week) VALUES (4, 'TUESDAY');