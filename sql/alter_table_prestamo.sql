alter table prestamo
add column pre_tipo VARCHAR(15) NOT NULL DEFAULT 'MENSUAL';

alter table prestamo
add column pre_cant_meses int(11) NOT NULL DEFAULT 12;

alter table prestamo
add column pre_cuota_pura decimal(11) NOT NULL DEFAULT 0;

alter table prestamo
drop column pre_importe_cuota;

alter table prestamo
add column pre_monto_total decimal(11) NOT NULL DEFAULT 0;

alter table cuota drop column cuo_intereses;