import { useEffect, useState } from 'react'
import { api } from '../api/client'
import Alert, { shouldShowAlert } from '../components/Alert'
import DataTable from '../components/DataTable'
import DetailPanel from '../components/DetailPanel'
import PlantingCycleForm from '../components/PlantingCycleForm'
import StatusActions from '../components/StatusActions'
import StatusBadge from '../components/StatusBadge'
import TableActions from '../components/TableActions'

export default function PlantingCyclesPage() {
  const [page, setPage] = useState(null)
  const [pageNumber, setPageNumber] = useState(0)
  const [batches, setBatches] = useState([])
  const [growers, setGrowers] = useState([])
  const [statusRows, setStatusRows] = useState(null)
  const [rangeRows, setRangeRows] = useState(null)
  const [stats, setStats] = useState([])
  const [result, setResult] = useState(null)
  const [editCycle, setEditCycle] = useState(null)
  const [detail, setDetail] = useState(null)

  function load(nextPage = pageNumber) {
    api(`/api/planting-cycles/page?page=${nextPage}&size=5&sort=plantingDate,desc`).then((response) => {
      if (response.status === 200) {
        setPage(response.body)
      }
    })
    api('/api/planting-cycles/stats/by-status').then((response) => {
      if (response.status === 200) {
        setStats(response.body)
      }
    })
    api('/api/crop-batches/all').then((response) => response.status === 200 && setBatches(response.body))
    api('/api/growers/all').then((response) => response.status === 200 && setGrowers(response.body))
  }

  useEffect(() => {
    load(0)
  }, [])

  async function save({ editId, body }) {
    const response = editId
      ? await api(`/api/planting-cycles/update/${editId}`, { method: 'PUT', body })
      : await api('/api/planting-cycles/save', { method: 'POST', body })
    setResult(response)
    if (response.status === 200 || response.status === 201) {
      setEditCycle(null)
      load(pageNumber)
    }
  }

  async function view(id) {
    const response = await api(`/api/planting-cycles/${id}`)
    if (response.status === 200) {
      const cycle = response.body
      setDetail([
        ['Planting date', cycle.plantingDate],
        ['Status', cycle.status],
        ['Trays', cycle.trays],
        ['Batch', cycle.cropBatch?.batchCode],
        ['Grower', `${cycle.grower?.firstName || ''} ${cycle.grower?.lastName || ''}`.trim()],
      ])
    } else {
      setResult(response)
    }
  }

  async function changeStatus(id, status) {
    const response = await api(`/api/planting-cycles/${id}/status?status=${status}`, { method: 'PATCH' })
    setResult(response)
    load(pageNumber)
  }

  async function remove(cycle) {
    if (!window.confirm(`Delete planting cycle on ${cycle.plantingDate}?`)) {
      return
    }
    const response = await api(`/api/planting-cycles/delete/${cycle.id}`, { method: 'DELETE' })
    setResult(response)
    if (response.status === 200) {
      if (editCycle?.id === cycle.id) {
        setEditCycle(null)
      }
      load(pageNumber)
    }
  }

  async function byStatus(event) {
    event.preventDefault()
    const status = new FormData(event.currentTarget).get('status')
    const response = await api(`/api/planting-cycles/by-status?status=${status}`)
    if (shouldShowAlert(response)) {
      setResult(response)
    } else {
      setResult(null)
      setStatusRows(response.body)
    }
  }

  async function between(event) {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    const response = await api(`/api/planting-cycles/between?start=${data.get('start')}&end=${data.get('end')}`)
    if (shouldShowAlert(response)) {
      setResult(response)
    } else {
      setResult(null)
      setRangeRows(response.body)
    }
  }

  async function cancelDay(event) {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    const response = await api(
      `/api/planting-cycles/cancel-day?growerId=${data.get('growerId')}&date=${data.get('date')}`,
      { method: 'PATCH' },
    )
    setResult(response)
    load(pageNumber)
  }

  function go(next) {
    setPageNumber(next)
    load(next)
  }

  const rows = page?.content || []

  return (
    <section>
      <h2>Planting cycles</h2>
      <Alert result={result} onDismiss={() => setResult(null)} />
      <DetailPanel title="Planting cycle details" fields={detail} onClose={() => setDetail(null)} />
      <div className="panel">
        <PlantingCycleForm
          batches={batches}
          growers={growers}
          editCycle={editCycle}
          onSubmit={save}
          onCancelEdit={() => setEditCycle(null)}
        />
      </div>
      {stats.length > 0 && (
        <p className="info-banner">Status counts: {stats.map((pair) => `${pair[0]} ${pair[1]}`).join(', ')}</p>
      )}
      <DataTable
        title="Planting cycles"
        subtitle={`Page ${pageNumber + 1} of ${page?.totalPages ?? 0} · ${page?.totalElements ?? 0} total`}
        isEmpty={rows.length === 0}
        emptyMessage="No planting cycles on this page."
      >
        <thead>
          <tr>
            <th>Date</th>
            <th className="col-center">Status</th>
            <th className="col-num">Trays</th>
            <th>Batch</th>
            <th>Grower</th>
            <th className="col-center">Workflow</th>
            <th className="col-actions">Actions</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((cycle) => (
            <tr key={cycle.id}>
              <td className="cell-strong">{cycle.plantingDate}</td>
              <td className="col-center">
                <StatusBadge status={cycle.status} />
              </td>
              <td className="col-num">{cycle.trays}</td>
              <td>{cycle.cropBatch?.batchCode}</td>
              <td>
                {cycle.grower?.firstName} {cycle.grower?.lastName}
              </td>
              <td className="col-center">
                <StatusActions status={cycle.status} onChange={(status) => changeStatus(cycle.id, status)} />
              </td>
              <td className="col-actions">
                <TableActions
                  onView={() => view(cycle.id)}
                  onEdit={() => {
                    setEditCycle(cycle)
                    setDetail(null)
                  }}
                  onDelete={() => remove(cycle)}
                />
              </td>
            </tr>
          ))}
        </tbody>
      </DataTable>
      <div className="row-actions">
        <button type="button" disabled={pageNumber <= 0} onClick={() => go(pageNumber - 1)}>
          Previous
        </button>
        <button type="button" disabled={!page || pageNumber + 1 >= page.totalPages} onClick={() => go(pageNumber + 1)}>
          Next
        </button>
      </div>
      <form className="form panel" onSubmit={byStatus}>
        <label>
          Status
          <select name="status" defaultValue="PLANNED">
            <option>PLANNED</option>
            <option>GROWING</option>
            <option>HARVESTED</option>
            <option>FAILED</option>
          </select>
        </label>
        <button type="submit">Cycles by status</button>
      </form>
      {statusRows && <p className="info-banner">Status query returned {statusRows.length} cycles.</p>}
      <form className="form panel" onSubmit={between}>
        <label>
          Start
          <input name="start" type="date" defaultValue="2026-10-01" required />
        </label>
        <label>
          End
          <input name="end" type="date" defaultValue="2026-10-31" required />
        </label>
        <button type="submit">Cycles in range</button>
      </form>
      {rangeRows && <p className="info-banner">Date range returned {rangeRows.length} cycles.</p>}
      <form className="form panel" onSubmit={cancelDay}>
        <label>
          Grower absent
          <select name="growerId" required>
            {growers.map((grower) => (
              <option key={grower.id} value={grower.id}>
                {grower.firstName} {grower.lastName}
              </option>
            ))}
          </select>
        </label>
        <label>
          Date
          <input name="date" type="date" required />
        </label>
        <button type="submit" className="secondary">
          Cancel that day
        </button>
      </form>
    </section>
  )
}
