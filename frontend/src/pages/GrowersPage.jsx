import { useEffect, useState } from 'react'
import { api } from '../api/client'
import Alert from '../components/Alert'
import DataTable from '../components/DataTable'
import DetailPanel from '../components/DetailPanel'
import TableActions from '../components/TableActions'
import CropFamilyPicker from '../components/CropFamilyPicker'

const emptyGrower = { firstName: '', lastName: '', phone: '', towerId: '', familyIds: [] }

export default function GrowersPage() {
  const [rows, setRows] = useState([])
  const [towers, setTowers] = useState([])
  const [families, setFamilies] = useState([])
  const [free, setFree] = useState([])
  const [result, setResult] = useState(null)
  const [createForm, setCreateForm] = useState(emptyGrower)
  const [editForm, setEditForm] = useState(null)
  const [detail, setDetail] = useState(null)

  function load() {
    api('/api/growers/all').then((response) => response.status === 200 && setRows(response.body))
    api('/api/grow-towers/all').then((response) => response.status === 200 && setTowers(response.body))
    api('/api/crop-families/all').then((response) => response.status === 200 && setFamilies(response.body))
    api('/api/grow-towers/free').then((response) => response.status === 200 && setFree(response.body))
  }

  useEffect(load, [])

  async function create(event) {
    event.preventDefault()
    if (createForm.familyIds.length === 0) {
      setResult({ status: 400, body: 'Select at least one crop family.' })
      return
    }
    const response = await api('/api/growers/save', {
      method: 'POST',
      body: {
        firstName: createForm.firstName,
        lastName: createForm.lastName,
        phone: createForm.phone,
        growTower: { id: createForm.towerId },
        cropFamilies: createForm.familyIds.map((id) => ({ id })),
      },
    })
    setResult(response)
    if (response.status === 201) {
      setCreateForm(emptyGrower)
      load()
    }
  }

  async function update(event) {
    event.preventDefault()
    if (!editForm) {
      return
    }
    const response = await api(`/api/growers/update/${editForm.id}`, {
      method: 'PUT',
      body: {
        firstName: editForm.firstName,
        lastName: editForm.lastName,
        phone: editForm.phone,
      },
    })
    setResult(response)
    if (response.status === 200) {
      setEditForm(null)
      load()
    }
  }

  async function view(id) {
    const response = await api(`/api/growers/${id}`)
    if (response.status === 200) {
      const grower = response.body
      setDetail([
        ['Name', `${grower.firstName} ${grower.lastName}`],
        ['Phone', grower.phone],
        ['Tower', grower.growTower ? `Tower ${grower.growTower.towerNumber}` : 'None'],
        ['Crop families', (grower.cropFamilies || []).map((family) => family.name).join(', ') || 'None'],
      ])
    } else {
      setResult(response)
    }
  }

  function startEdit(row) {
    setEditForm({
      id: row.id,
      firstName: row.firstName,
      lastName: row.lastName,
      phone: row.phone,
    })
    setDetail(null)
  }

  async function remove(row) {
    if (!window.confirm(`Delete grower ${row.firstName} ${row.lastName}?`)) {
      return
    }
    const response = await api(`/api/growers/delete/${row.id}`, { method: 'DELETE' })
    setResult(response)
    if (response.status === 200) {
      load()
      if (editForm?.id === row.id) {
        setEditForm(null)
      }
    }
  }

  async function addFamily(event) {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    const response = await api(`/api/growers/${data.get('growerId')}/crop-families/${data.get('familyId')}`, {
      method: 'PUT',
    })
    setResult(response)
    load()
  }

  async function moveTower(event) {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    const response = await api(
      `/api/growers/${data.get('growerId')}/grow-tower?towerNumber=${data.get('towerNumber')}`,
      { method: 'PATCH' },
    )
    setResult(response)
    load()
  }

  return (
    <section>
      <h2>Growers</h2>
      <Alert result={result} onDismiss={() => setResult(null)} />
      <DetailPanel title="Grower details" fields={detail} onClose={() => setDetail(null)} />
      <form className="form panel" onSubmit={create}>
        <div className="form-heading">
          <strong>Create grower</strong>
        </div>
        <label>
          First name
          <input value={createForm.firstName} onChange={(event) => setCreateForm({ ...createForm, firstName: event.target.value })} required />
        </label>
        <label>
          Last name
          <input value={createForm.lastName} onChange={(event) => setCreateForm({ ...createForm, lastName: event.target.value })} required />
        </label>
        <label>
          Phone
          <input value={createForm.phone} onChange={(event) => setCreateForm({ ...createForm, phone: event.target.value })} required />
        </label>
        <label>
          Tower
          <select value={createForm.towerId} onChange={(event) => setCreateForm({ ...createForm, towerId: event.target.value })} required>
            <option value="">Select tower</option>
            {towers.map((tower) => (
              <option key={tower.id} value={tower.id}>
                {tower.towerNumber} {tower.name}
              </option>
            ))}
          </select>
        </label>
        <label>
          Crop families
          <CropFamilyPicker
            families={families}
            selectedIds={createForm.familyIds}
            onChange={(familyIds) => setCreateForm({ ...createForm, familyIds })}
            required
          />
        </label>
        <button type="submit">Save grower</button>
      </form>
      {editForm && (
        <form className="form panel" onSubmit={update}>
          <div className="form-heading">
            <strong>Update grower</strong>
            <button type="button" className="small ghost" onClick={() => setEditForm(null)}>
              Cancel edit
            </button>
          </div>
          <label>
            First name
            <input value={editForm.firstName} onChange={(event) => setEditForm({ ...editForm, firstName: event.target.value })} required />
          </label>
          <label>
            Last name
            <input value={editForm.lastName} onChange={(event) => setEditForm({ ...editForm, lastName: event.target.value })} required />
          </label>
          <label>
            Phone
            <input value={editForm.phone} onChange={(event) => setEditForm({ ...editForm, phone: event.target.value })} required />
          </label>
          <button type="submit">Update grower</button>
        </form>
      )}
      <form className="form panel" onSubmit={addFamily}>
        <label>
          Grower
          <select name="growerId" required>
            {rows.map((grower) => (
              <option key={grower.id} value={grower.id}>
                {grower.firstName} {grower.lastName}
              </option>
            ))}
          </select>
        </label>
        <label>
          Add crop family
          <select name="familyId" required>
            {families.map((family) => (
              <option key={family.id} value={family.id}>
                {family.name}
              </option>
            ))}
          </select>
        </label>
        <button type="submit">Add family</button>
      </form>
      <form className="form panel" onSubmit={moveTower}>
        <label>
          Grower
          <select name="growerId" required>
            {rows.map((grower) => (
              <option key={grower.id} value={grower.id}>
                {grower.firstName} {grower.lastName}
              </option>
            ))}
          </select>
        </label>
        <label>
          Tower number
          <input name="towerNumber" type="number" required />
        </label>
        <button type="submit">Move tower</button>
      </form>
      {free.length > 0 && <p className="info-banner">Free towers: {free.map((tower) => tower.towerNumber).join(', ')}</p>}
      <DataTable title="Grower roster" subtitle={`${rows.length} growers`} isEmpty={rows.length === 0} emptyMessage="No growers yet.">
        <thead>
          <tr>
            <th>Name</th>
            <th>Phone</th>
            <th className="col-center">Tower</th>
            <th>Crop families</th>
            <th className="col-actions">Actions</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((grower) => (
            <tr key={grower.id}>
              <td className="cell-strong">
                {grower.firstName} {grower.lastName}
              </td>
              <td>{grower.phone}</td>
              <td className="col-center">{grower.growTower ? grower.growTower.towerNumber : '—'}</td>
              <td>{(grower.cropFamilies || []).map((family) => family.name).join(', ') || '—'}</td>
              <td className="col-actions">
                <TableActions onView={() => view(grower.id)} onEdit={() => startEdit(grower)} onDelete={() => remove(grower)} />
              </td>
            </tr>
          ))}
        </tbody>
      </DataTable>
    </section>
  )
}
