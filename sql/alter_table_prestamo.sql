alter table prestamo
add column pre_tipo VARCHAR(15) NOT NULL DEFAULT 'MENSUAL';

alter table prestamo
add column pre_cant_meses int(11) NOT NULL DEFAULT 12;

alter table prestamo
drop column pre_importe_cuota;

alter table cuota drop column cuo_intereses;