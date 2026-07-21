CREATE OR REPLACE FUNCTION vota_mas.set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON vota_mas.users
    FOR EACH ROW EXECUTE FUNCTION vota_mas.set_updated_at();

CREATE TRIGGER trg_role_updated_at
    BEFORE UPDATE ON vota_mas.role
    FOR EACH ROW EXECUTE FUNCTION vota_mas.set_updated_at();

CREATE TRIGGER trg_module_updated_at
    BEFORE UPDATE ON vota_mas.module
    FOR EACH ROW EXECUTE FUNCTION vota_mas.set_updated_at();

CREATE TRIGGER trg_permission_updated_at
    BEFORE UPDATE ON vota_mas.permission
    FOR EACH ROW EXECUTE FUNCTION vota_mas.set_updated_at();

CREATE TRIGGER trg_voting_zones_updated_at
    BEFORE UPDATE ON vota_mas.voting_zones
    FOR EACH ROW EXECUTE FUNCTION vota_mas.set_updated_at();

CREATE TRIGGER trg_polling_places_updated_at
    BEFORE UPDATE ON vota_mas.polling_places
    FOR EACH ROW EXECUTE FUNCTION vota_mas.set_updated_at();

CREATE TRIGGER trg_voting_tables_updated_at
    BEFORE UPDATE ON vota_mas.voting_tables
    FOR EACH ROW EXECUTE FUNCTION vota_mas.set_updated_at();

CREATE TRIGGER trg_potential_voters_updated_at
    BEFORE UPDATE ON vota_mas.potential_voters
    FOR EACH ROW EXECUTE FUNCTION vota_mas.set_updated_at();
