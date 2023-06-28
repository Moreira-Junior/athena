INSERT INTO public.users(
	username, password, enabled)
	VALUES ('admin', '$2a$10$cNICrY7U4j6Nz1g/WsQvJu81adHOGN/Gl041bwt1Z80FO/I720Em2', 'true');

INSERT INTO public.authorities(
	username, authority)
	VALUES ('admin', 'ROLE_ADMIN');