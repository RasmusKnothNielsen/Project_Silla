SELECT ghtm.interpolation_id,
       ghtm.statid,
       ghtm.timeobs,
       ghtm.temperature_mean AS elem_val,
       101 AS elem_no,
       ghtm.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_temperature_mean ghtm
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghtm.interpolation_id)))
UNION ALL
SELECT ghtm.interpolation_id,
       ghtm.statid,
       ghtm.timeobs,
       ghtm.temperature_min AS elem_val,
       122 AS elem_no,
       ghtm.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_temperature_min ghtm
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghtm.interpolation_id)))
UNION ALL
SELECT ghtm.interpolation_id,
       ghtm.statid,
       ghtm.timeobs,
       ghtm.temperature_min12h AS elem_val,
       123 AS elem_no,
       ghtm.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_temperature_min12h ghtm
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghtm.interpolation_id)))
UNION ALL
SELECT ghtm.interpolation_id,
       ghtm.statid,
       ghtm.timeobs,
       ghtm.temperature_max AS elem_val,
       112 AS elem_no,
       ghtm.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_temperature_max ghtm
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghtm.interpolation_id)))
UNION ALL
SELECT ghtm.interpolation_id,
       ghtm.statid,
       ghtm.timeobs,
       ghtm.temperature_max12h AS elem_val,
       113 AS elem_no,
       ghtm.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_temperature_max12h ghtm
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghtm.interpolation_id)))
UNION ALL
SELECT ghtm.interpolation_id,
       ghtm.statid,
       ghtm.timeobs,
       ghtm.relative_humidity_mean AS elem_val,
       201 AS elem_no,
       ghtm.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_relative_humidity_mean ghtm
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghtm.interpolation_id)))
UNION ALL
SELECT ghtm.interpolation_id,
       ghtm.statid,
       ghtm.timeobs,
       ghtm.wind_speed_mean AS elem_val,
       301 AS elem_no,
       ghtm.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_wind_speed_mean ghtm
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghtm.interpolation_id)))
UNION ALL
SELECT ghtm.interpolation_id,
       ghtm.statid,
       ghtm.timeobs,
       ghtm.wind_speed_3sec_max AS elem_val,
       305 AS elem_no,
       ghtm.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_wind_speed_3sec_max ghtm
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghtm.interpolation_id)))
UNION ALL
SELECT ghtm.interpolation_id,
       ghtm.statid,
       ghtm.timeobs,
       ghtm.wind_direction_mean AS elem_val,
       371 AS elem_no,
       ghtm.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_wind_direction_mean ghtm
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghtm.interpolation_id)))
UNION ALL
SELECT ghtm.interpolation_id,
       ghtm.statid,
       ghtm.timeobs,
       ghtm.wind_direction_mean_10min AS elem_val,
       365 AS elem_no,
       ghtm.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_wind_direction_mean_10min ghtm
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghtm.interpolation_id)))
UNION ALL
SELECT ghtm.interpolation_id,
       ghtm.statid,
       ghtm.timeobs,
       ghtm.pressure_mean AS elem_val,
       401 AS elem_no,
       ghtm.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_pressure_mean ghtm
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghtm.interpolation_id)))
UNION ALL
SELECT ghtm.interpolation_id,
       ghtm.statid,
       ghtm.timeobs,
       ghtm.sunshine_sum AS elem_val,
       504 AS elem_no,
       ghtm.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_sunshine_sum ghtm
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghtm.interpolation_id)))
UNION ALL
SELECT ghtm.interpolation_id,
       ghtm.statid,
       ghtm.timeobs,
       ghtm.radiation_mean AS elem_val,
       550 AS elem_no,
       ghtm.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_radiation_mean ghtm
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghtm.interpolation_id)))
UNION ALL
SELECT ghps.interpolation_id,
       ghps.statid,
       ghps.timeobs,
       ghps.precipitation_sum AS elem_val,
       601 AS elem_no,
       ghps.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_precipitation_sum ghps
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghps.interpolation_id)))
UNION ALL
SELECT ghps.interpolation_id,
       ghps.statid,
       ghps.timeobs,
       ghps.precipitation_sum12h AS elem_val,
       603 AS elem_no,
       ghps.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_precipitation_sum12h ghps
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghps.interpolation_id)))
UNION ALL
SELECT ghps.interpolation_id,
       ghps.statid,
       ghps.timeobs,
       ghps.precipitation_sum24h AS elem_val,
       609 AS elem_no,
       ghps.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_precipitation_sum24h ghps
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghps.interpolation_id)))
UNION ALL
SELECT ghps.interpolation_id,
       ghps.statid,
       ghps.timeobs,
       ghps.cloud_cover AS elem_val,
       801 AS elem_no,
       ghps.label,
       ghdi.calc_date,
       ghdi.ins_date
FROM (view_basis_hourly_na_cloud_cover ghps
         LEFT JOIN basis_hourly_na_interpolation ghdi ON ((ghdi.interpolation_id = ghps.interpolation_id)));
