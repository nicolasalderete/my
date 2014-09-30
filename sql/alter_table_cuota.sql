alter table cuota
add column cuo_pago_parcial decimal(11) NOT NULL DEFAULT 0;

alter table cuota
add column cuo_pura decimal(11) NOT NULL DEFAULT 0;

alter table cuota
drop column cuo_total

alter table cuota
add column cuo_interes decimal(11) NOT NULL DEFAULT 0;

alter table cuota
drop column cuo_sal_favor;


alter table cuota
drop column cuo_tasa_mensual;